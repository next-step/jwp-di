package core.di.factory.generator;

import core.annotation.Inject;
import core.di.exception.BeanCreateException;
import core.di.exception.NoDefaultConstructorException;
import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

public class ConstructorTypeGenerator extends AbstractBeanGenerator {
    @Override
    public boolean support(BeanInitInfo beanInitInfo) {
        BeanType beanType = beanInitInfo.getBeanType();

        return beanType.isComponentType() || beanType == BeanType.CONFIGURATION;
    }

    @Override
    Object generateBean(Set<Class<?>> dependency, BeanFactory beanFactory, BeanInitInfo beanInitInfo) {
        Class<?> type = beanInitInfo.getClassType();

        Object instance;
        try {
            // create constructor
            Constructor<?> constructor = getInjectAttachedConstructor(type);

            // get bean of parameters to use as a argument
            Object[] arguments = getArguments(dependency, beanFactory, constructor.getParameterTypes());

            // create new instance
            constructor.setAccessible(true);
            instance = constructor.newInstance(arguments);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreateException("Fail to create bean of " + type.getName() + " cuz : " + e.getMessage());
        }

        return instance;
    }

    private Constructor<?> getInjectAttachedConstructor(Class<?> type) {
        return Arrays.stream(type.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .findFirst()
                .orElseGet(() -> getDefaultConstructor(type));
    }

    private Constructor<?> getDefaultConstructor(Class<?> type) {
        try {
            return type.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new NoDefaultConstructorException(type);
        }
    }
}
