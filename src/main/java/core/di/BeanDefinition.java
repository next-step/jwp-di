package core.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import core.di.factory.BeanFactoryUtils;

public class BeanDefinition {

    private final Constructor<?> constructor;
    private final Method method;
    private final String name;

    public BeanDefinition(Class<?> clazz) {
        this(clazz, null);
    }

    public BeanDefinition(Class<?> beanClass, Method method) {
        this.constructor = BeanFactoryUtils.getInjectedConstructor(beanClass);
        this.method = method;
        this.name = BeanNameGenerator.generateBeanName(beanClass.getSimpleName());
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

    public String getBeanName() {
        return name;
    }

    public Autowire getResolvedAutowireMode() {
        if (constructor != null) {
            return Autowire.CONSTRUCTOR;
        }
        return Autowire.NO;
    }
}
