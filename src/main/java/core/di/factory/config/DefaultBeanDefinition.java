package core.di.factory.config;

import core.annotation.Inject;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class DefaultBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;
    private Optional<Constructor<?>> injectConstructor;

    public DefaultBeanDefinition(Class<?> clazz) {
        this.beanClass = clazz;
        injectConstructor = initInjectConstructor();
    }

    private Optional<Constructor<?>> initInjectConstructor() {
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        return Arrays.stream(declaredConstructors).filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .findFirst();
    }

    @Override
    public Optional<Constructor<?>> getInjectConstructor() {
        return injectConstructor;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public boolean isAnnotatedDefinition() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultBeanDefinition that = (DefaultBeanDefinition) o;
        return Objects.equals(beanClass, that.beanClass) &&
                Objects.equals(injectConstructor, that.injectConstructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanClass, injectConstructor);
    }
}
