package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

import static org.reflections.ReflectionUtils.getAllConstructors;
import static org.reflections.ReflectionUtils.withAnnotation;

public class BeanFactoryUtils {

    private BeanFactoryUtils() {
        throw new AssertionError();
    }

    /**
     * 인자로 전달하는 클래스의 생성자 중 @Inject 애노테이션이 설정되어 있는 생성자를 반환
     * `@Inject` 애노테이션이 설정되어 있는 생성자는 클래스당 하나로 가정한다.
     *
     * @param clazz 생성자를 찾고자 하는 클래스
     * @return @Inject 애너테이션이 적용된 첫 번째 생성자
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Constructor getInjectedConstructor(Class<?> clazz) {
        Set<Constructor> injectedConstructors = getAllConstructors(clazz, withAnnotation(Inject.class));
        if (injectedConstructors.isEmpty()) {
            return null;
        }
        validateConstructorsCount(injectedConstructors);

        return injectedConstructors.iterator().next();
    }

    @SuppressWarnings("rawtypes")
    private static void validateConstructorsCount(final Set<Constructor> injectedConstructors) {
        final int constructorsCount = injectedConstructors.size();
        if (constructorsCount > 1) {
            throw new IllegalStateException("'@Inject' 애너테이션이 적용된 생성자는 반드시 1개만 존재해야 합니다. 생성자 수 : " + constructorsCount);
        }
    }

    /**
     * 인자로 전달되는 클래스의 구현 클래스. 만약 인자로 전달되는 Class가 인터페이스가 아니면 전달되는 인자가 구현 클래스,
     * 인터페이스인 경우 BeanFactory가 관리하는 모든 클래스 중에 인터페이스를 구현하는 클래스를 찾아 반환
     *
     * @param injectedClazz 생성자의 인자로 전달되는 클래스
     * @param preInstanticateBeans BeanFactory 에 등록된 인스턴스 목록
     * @return 생성자의 인자로 전달되는 타입의 구현체 또는 콘크리트 클래스
     */
    public static Class<?> findConcreteClass(Class<?> injectedClazz, Set<Class<?>> preInstanticateBeans) {
        if (!injectedClazz.isInterface()) {
            return injectedClazz;
        }

        return findImplementedConcreteClass(injectedClazz, preInstanticateBeans);
    }

    public static Method findConcreteMethod(Class<?> parameterType, Set<Method> preInstanticateMethodBeans) {
        if (!parameterType.isInterface()) {
            return preInstanticateMethodBeans.stream()
                .filter(method -> method.getReturnType().equals(parameterType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 메서드를 찾을 수 없습니다."));
        }

        return findImplementedConcreteMethodBean(parameterType, preInstanticateMethodBeans);
    }

    private static Class<?> findImplementedConcreteClass(final Class<?> injectedClazz, final Set<Class<?>> preInstanticateBeans) {
        return preInstanticateBeans.stream()
            .filter(bean -> contains(bean.getInterfaces(), injectedClazz))
            .findAny()
            .orElseThrow(() -> new IllegalStateException(injectedClazz + " 인터페이스를 구현하는 Bean이 존재하지 않는다."));
    }

    private static Method findImplementedConcreteMethodBean(final Class<?> returnType, final Set<Method> preInstanticateMethodBeans) {
        return preInstanticateMethodBeans.stream()
            .filter(methodBean -> methodBean.getReturnType().isInterface())
            .findAny()
            .orElseThrow(() -> new IllegalStateException(returnType + " 인터페이스를 구현하는 Bean이 존재하지 않는다."));
    }

    private static boolean contains(final Class<?>[] interfaces, final Class<?> injectedClazz) {
        return Sets.newHashSet(interfaces).contains(injectedClazz);
    }
}
