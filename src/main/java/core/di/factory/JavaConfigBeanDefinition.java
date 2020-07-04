 package core.di.factory;

import core.annotation.Lazy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JavaConfigBeanDefinition implements BeanDefinition {

    private final Class<?> originClass;
    private final List<Class<?>> dependencies = new ArrayList<>();
    private final Method method;

    public JavaConfigBeanDefinition(Method method) {
        originClass = method.getReturnType();
        Collections.addAll(dependencies, method.getParameterTypes());
        this.method = method;
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
}
