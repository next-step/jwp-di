package core.di.factory.definition;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import com.google.common.collect.Sets;

public interface BeanDefinition {

    Class<?> getBeanClass();

    Class<?>[] getParameterTypes();

    Object createObject(Object[] parameters) throws
        InstantiationException,
        IllegalAccessException,
        InvocationTargetException;

    boolean notCreatable();

    int getParameterCount();

    default boolean isConcreteClass(Class<?> injectedClazz) {
        Set<Class<?>> interfaces = Sets.newHashSet(getBeanClass().getInterfaces());

        return interfaces.contains(injectedClazz);
    }
}
