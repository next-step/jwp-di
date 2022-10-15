package core.di.factory.bean;

import core.di.factory.BeanFactoryUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanCreationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNullElseGet;

public class ClassBean implements Bean {
    private final Class<?> clazz;

    public ClassBean(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<?> getType() {
        return clazz;
    }

    @Override
    public List<Class<?>> getParameterTypes() {
        return List.of(getConstructor().getParameterTypes());
    }

    private Constructor<?> getConstructor() {
        return requireNonNullElseGet(BeanFactoryUtils.getInjectedConstructor(clazz), () -> getDefaultConstructor(clazz));
    }

    private Constructor<?> getDefaultConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(clazz.getName(), e);
        }
    }

    @Override
    public boolean isNotInstanced() {
        return clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
    }

    @Override
    public Object instantiate(List<Object> args) {
        if (isNotInstanced()) {
            throw new IllegalArgumentException();
        }
        return BeanUtils.instantiateClass(getConstructor(), args.toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassBean classBean = (ClassBean) o;
        return Objects.equals(clazz, classBean.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public String toString() {
        return "ClassBean{" +
                "clazz=" + clazz +
                '}';
    }
}
