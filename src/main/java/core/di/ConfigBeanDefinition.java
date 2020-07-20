package core.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by iltaek on 2020/07/19 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class ConfigBeanDefinition implements BeanDefinition {

    private final Class<?> beanClass;
    private final Method method;

    public ConfigBeanDefinition(Class<?> beanClass, Method method) {
        this.beanClass = beanClass;
        this.method = method;
    }

    @Override
    public Constructor<?> getBeanConstructor() {
        return null;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public Method getMethod() {
        return method;
    }
}
