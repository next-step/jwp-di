package core.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import core.di.factory.BeanFactoryUtils;

public class BeanDefinition {

    private final Class<?> clazz;
    private final Constructor<?> constructor;
    private final Method method;

    public BeanDefinition(Class<?> clazz) {
        this(clazz, null);
    }

    public BeanDefinition(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        this.method = method;
    }

    public boolean hasBeanMethod() {
        return method != null;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public Method getMethod() {
        return method;
    }

    public Autowire getResolvedAutowireMode() {
        if (constructor != null) {
            return Autowire.CONSTRUCTOR;
        }
        return Autowire.NO;
    }
}
