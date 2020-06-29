package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: 써보다가 마음에 안들면 책임을 DefaultBeanDefinition으로 이동하자.
 * @author hyeyoom
 */
public final class BeanDefinitionUtil {

    public static Map<Class<?>, BeanDefinition> convertClassToDefinition(Collection<Class<?>> classes) {
        final Map<Class<?>, BeanDefinition> definitionMap = Maps.newHashMap();
        for (Class<?> clazz : classes) {
            definitionMap.put(clazz, buildBeanDefinition(clazz));
        }
        return definitionMap;
    }

    public static BeanDefinition buildBeanDefinition(Class<?> clazz) {
        final DefaultBeanDefinition beanDefinition = new DefaultBeanDefinition(clazz);
        final Optional<Constructor<?>> maybeCtor = Optional.ofNullable(findBeanConstructor(clazz));
        if (maybeCtor.isPresent()) {
            final Constructor<?> ctor = maybeCtor.get();
            beanDefinition.setDependencies(ctor.getParameterTypes());
            beanDefinition.setBeanConstructor(ctor);
        }
        return beanDefinition;
    }

    private static Constructor<?> findBeanConstructor(Class<?> clazz) {
        final Constructor<?>[] ctors = clazz.getDeclaredConstructors();
        for (Constructor<?> ctor : ctors) {
            if (ctor.isAnnotationPresent(Inject.class)) {
                return ctor;
            }
        }
        return null;
    }
}
