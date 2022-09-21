package core.di.factory.definition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConfigurationBeanDefinition implements BeanDefinition {

    private Class<?> clazz;
    private Method method;

    public ConfigurationBeanDefinition(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
    }

    @Override
    public Class<?> getClazz() {
        return this.clazz;
    }

    @Override
    public Class<?>[] parameterTypes() {
        return method.getParameterTypes();
    }

    @Override
    public Object instantiate(Object[] objects) {
        try {
            return this.method.invoke(clazz.getDeclaredConstructor().newInstance(), objects);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
