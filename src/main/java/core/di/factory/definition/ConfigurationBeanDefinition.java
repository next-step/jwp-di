package core.di.factory.definition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConfigurationBeanDefinition implements BeanDefinition {

    private final Object instance;
    private final Method method;

    public ConfigurationBeanDefinition(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    @Override
    public Class<?> getBeanClass() {
        return method.getReturnType();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    @Override
    public Object createObject(Object[] parameters) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(instance, parameters);
    }

    @Override
    public boolean notCreatable() {
        return false;
    }

    @Override
    public int getParameterCount() {
        return method.getParameterCount();
    }

    @Override
    public String toString() {
        return "ConfigurationBeanDefinition{" +
            "instance=" + instance +
            ", method=" + method +
            '}';
    }
}
