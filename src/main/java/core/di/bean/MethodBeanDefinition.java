package core.di.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by hspark on 2019-09-01.
 */
public class MethodBeanDefinition implements BeanDefinition {
    private Object configuration;
    private Class<?> beanClass;
    private Method beanMethod;

    public MethodBeanDefinition(Object configuration, Method beanMethod) {
        this.configuration = configuration;
        this.beanClass = beanMethod.getReturnType();
        this.beanMethod = beanMethod;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Constructor<?> getInjectedConstructor() {
        return null;
    }

    @Override
    public Class[] getInjectParameterClasses() {
        return beanMethod.getParameterTypes();
    }

    public Method getBeanMethod() {
        return beanMethod;
    }


    public Object getConfiguration() {
        return configuration;
    }
}
