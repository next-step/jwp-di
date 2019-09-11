package core.di.bean;

import core.di.BeanRegister;
import org.springframework.beans.factory.BeanCreationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class AnnotationBeanDefinition implements BeanDefinition {

    private Object target;
    private Class<?> clazz;
    private final Method method;
    private final Class<?>[] parameters;

    public AnnotationBeanDefinition(Class<?> clazz, Method method) {
        this.target = initTarget(clazz);
        this.clazz = method.getReturnType();
        this.method = method;
        this.parameters = initParameters(method);
    }

    private Object initTarget(Class<?> annotationClazz) {
        try {
            return annotationClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanCreationException("Annotation class create fail");
        }
    }

    private Class<?>[] initParameters(Method method) {
        return method.getParameterTypes();
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Class<?>[] getParameters() {
        return parameters;
    }

    @Override
    public Object register(Object[] parameters) {
        return ((BeanRegister) params -> {
            try {
                return method.invoke(target, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return new BeanCreationException("Method Bean create fail");
            }
        }).newInstance(parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationBeanDefinition that = (AnnotationBeanDefinition) o;
        return Objects.equals(target, that.target) &&
                Objects.equals(clazz, that.clazz) &&
                Objects.equals(method, that.method) &&
                Arrays.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(target, clazz, method);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public String toString() {
        return "AnnotationBeanDefinition{" +
                "target=" + target +
                ", clazz=" + clazz +
                ", method=" + method +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
