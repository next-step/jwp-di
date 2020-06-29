package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;

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
        beanDefinition.setDependencies(findDependencies(clazz));
        return beanDefinition;
    }

    private static Class<?>[] findDependencies(Class<?> clazz) {
        final Constructor<?>[] ctors = clazz.getDeclaredConstructors();
        for (Constructor<?> ctor : ctors) {
            if (ctor.isAnnotationPresent(Inject.class)) {
                return ctor.getParameterTypes();
            }
        }
        return null;
    }

}
