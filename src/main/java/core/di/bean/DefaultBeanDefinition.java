package core.di.bean;

import core.di.factory.BeanFactoryUtils;
import core.di.BeanRegister;
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
            if (constructor.isPresent()) {
                return BeanUtils.instantiateClass(constructor.get(), params);
            }
            return BeanUtils.instantiateClass(clazz);
        }).newInstance(parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultBeanDefinition that = (DefaultBeanDefinition) o;
        return Objects.equals(clazz, that.clazz) &&
                Objects.equals(constructor, that.constructor) &&
                Arrays.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clazz, constructor);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
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
