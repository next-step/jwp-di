package core.di.tobe.bean;

import core.di.factory.BeanFactoryUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class DefaultBeanDefinition implements BeanDefinition {

    private final Class<?> clazz;
    private final Optional<Constructor<?>> constructor;
    private final Class<?>[] parameters;

    public DefaultBeanDefinition(Class<?> clazz) {
        this.clazz = clazz;
        this.constructor = initInjectConstructor(clazz);
        this.parameters = initInjectParameters();
    }

    private Optional<Constructor<?>> initInjectConstructor(Class<?> clazz) {
        return Optional.ofNullable(BeanFactoryUtils.getInjectedConstructor(clazz));
    }

    private Class<?>[] initInjectParameters() {
        if (!constructor.isPresent()) {
            return null;
        }
        return this.constructor.map(Constructor::getParameterTypes)
                .orElse(null);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Class<?>[] getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultBeanDefinition that = (DefaultBeanDefinition) o;
        return Objects.equals(clazz, that.clazz) &&
                Objects.equals(constructor, that.constructor) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, constructor, parameters);
    }

    @Override
    public String toString() {
        return "DefaultBeanDefinition{" +
                "clazz=" + clazz +
                ", constructor=" + constructor +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
