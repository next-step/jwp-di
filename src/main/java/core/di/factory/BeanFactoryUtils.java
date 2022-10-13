package core.di.factory;

import static org.reflections.ReflectionUtils.*;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import core.annotation.Inject;
import core.di.factory.definition.BeanDefinition;

public class BeanFactoryUtils {
    /**
     * 인자로 전달하는 클래스의 생성자 중 @Inject 애노테이션이 설정되어 있는 생성자를 반환
     *
     * @param clazz
     * @return
     * @Inject 애노테이션이 설정되어 있는 생성자는 클래스당 하나로 가정한다.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional<Constructor<?>> getInjectedConstructor(Class<?> clazz) {
        Set<Constructor> injectedConstructors = getAllConstructors(clazz, withAnnotation(Inject.class));
        if (injectedConstructors.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(injectedConstructors.iterator().next());
    }

    /**
     * 인자로 전달되는 클래스의 구현 클래스. 만약 인자로 전달되는 Class가 인터페이스가 아니면 전달되는 인자가 구현 클래스,
     * 인터페이스인 경우 BeanFactory가 관리하는 모든 클래스 중에 인터페이스를 구현하는 클래스를 찾아 반환
     *
     * @param injectedClazz
     * @param preInstanticateBeans
     * @return
     */
    public static BeanDefinition findConcreteClass(BeanDefinition injectedClazz, Set<BeanDefinition> preInstanticateBeans) {
        if (!injectedClazz.notCreatable()) {
            return injectedClazz;
        }

        for (var preInstanticateBean : preInstanticateBeans) {
            Class<?> beanClass = preInstanticateBean.getBeanClass();
            Set<Class<?>> interfaces = Sets.newHashSet(beanClass.getInterfaces());

            if (interfaces.contains(injectedClazz.getBeanClass())) {
                return preInstanticateBean;
            }
        }

        throw new IllegalStateException(injectedClazz + "인터페이스를 구현하는 Bean이 존재하지 않는다.");
    }

    public static <T> Class<?> findConcreteClass(Class<T> injectedClazz, Set<Class<?>> preInstanticateBeans) {
        if (!injectedClazz.isInterface()) {
            return injectedClazz;
        }

        for (var preInstanticateBean : preInstanticateBeans) {
            Set<Class<?>> interfaces = Sets.newHashSet(preInstanticateBean.getInterfaces());

            if (interfaces.contains(preInstanticateBean)) {
                return preInstanticateBean;
            }
        }

        throw new IllegalStateException(injectedClazz + "인터페이스를 구현하는 Bean이 존재하지 않는다.");
    }
}
