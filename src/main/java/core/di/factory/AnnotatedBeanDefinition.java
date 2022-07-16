package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AnnotatedBeanDefinition implements BeanDefinition {

    private final Class<?> configurationClass;
    private final Class<?> beanClass;
    private final Method method;

    public AnnotatedBeanDefinition(Class<?> configurationClass, Method method) {
        this.configurationClass = configurationClass;
        this.beanClass = method.getReturnType();
        this.method = method;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Constructor<?> getInjectedConstructor() {
        return null;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getConfigurationClass() {
        return configurationClass;
    }
}
