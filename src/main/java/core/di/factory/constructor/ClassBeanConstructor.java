package core.di.factory.constructor;

import core.di.factory.BeanFactoryUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNullElseGet;

public final class ClassBeanConstructor implements BeanConstructor {

    private final Class<?> clazz;

    private ClassBeanConstructor(Class<?> clazz) {
        Assert.notNull(clazz, "'clazz' must not be null");
        this.clazz = clazz;
    }

    public static ClassBeanConstructor from(Class<?> clazz) {
        return new ClassBeanConstructor(clazz);
    }

    @Override
    public Class<?> type() {
        return clazz;
    }

    @Override
    public List<Class<?>> parameterTypes() {
        return List.of(constructor().getParameterTypes());
    }

    @Override
    public boolean isNotInstanced() {
        return clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
    }

    @Override
    public Object instantiate(List<Object> args) {
        if (isNotInstanced()) {
            throw new IllegalStateException(String.format("constructor(%s) can not instanced", this));
        }
        return BeanUtils.instantiateClass(constructor(), args.toArray());
    }

    private Constructor<?> constructor() {
        return requireNonNullElseGet(BeanFactoryUtils.getInjectedConstructor(clazz), () -> defaultConstructor(clazz));
    }

    private Constructor<?> defaultConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(clazz.getName(), e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassBeanConstructor that = (ClassBeanConstructor) o;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public String toString() {
        return "ClassBeanConstructor{" +
                "clazz=" + clazz +
                '}';
    }
}
