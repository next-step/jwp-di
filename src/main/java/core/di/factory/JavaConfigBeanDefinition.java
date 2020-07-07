package core.di.factory;

import core.annotation.Lazy;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class JavaConfigBeanDefinition extends AbstractBeanDefinition {

    private final Method method;
    private final Class<?> configurationClass;

    public JavaConfigBeanDefinition(Method method) {
        super(method.getReturnType());
        Collections.addAll(dependencies, method.getParameterTypes());
        this.method = method;
        this.configurationClass = method.getDeclaringClass();
    }

    @Override
    public Class<?> getOriginalClass() {
        return originClass;
    }

    @Override
    public Constructor<?> getBeanConstructor() {
        return null;
    }

    @Override
    public void setBeanConstructor(Constructor<?> constructor) {

    }

    @Override
    public List<Class<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public void setDependencies(Class<?>... clazz) {
        Collections.addAll(dependencies, clazz);
    }

    @Override
    public boolean isLazyInit() {
        final Lazy lazy = method.getAnnotation(Lazy.class);
        if (lazy == null) {
            return false;
        }
        return lazy.value();
    }

    @Override
    public Object instantiate(List<Object> dependencies) {
        final Object configClassInstance = BeanUtils.instantiateClass(configurationClass);
        try {
            return method.invoke(configClassInstance, dependencies.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }
}
