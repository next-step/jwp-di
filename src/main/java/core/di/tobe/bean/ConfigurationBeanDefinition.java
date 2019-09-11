package core.di.tobe.bean;

import org.springframework.beans.factory.BeanCreationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class ConfigurationBeanDefinition implements BeanDefinition {

    private Object target;
    private Class<?> clazz;
    private final Method method;
    private final Class<?>[] parameters;

    public ConfigurationBeanDefinition(Class<?> clazz, Method method) {
        try {
            this.target = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.clazz = method.getReturnType();
        this.method = method;
        this.parameters = initParameters(method);
    }

    public Object newInstance(Object[] parameterList) {
        try {
            Object invoke = method.invoke(target, parameterList);
            this.clazz = method.getReturnType();
            return invoke;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new BeanCreationException("Bean 생성 실패");
    }

    private Class<?>[] initParameters(Method method) {
        return method.getParameterTypes();
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public Class<?>[] getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationBeanDefinition that = (ConfigurationBeanDefinition) o;
        return Objects.equals(clazz, that.clazz) &&
                Objects.equals(method, that.method) &&
                Arrays.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clazz, method);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public String toString() {
        return "ConfigurationBeanDefinition{" +
                "clazz=" + clazz +
                ", method=" + method +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
