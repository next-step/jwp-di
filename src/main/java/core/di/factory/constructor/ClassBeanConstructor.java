package core.di.factory.constructor;

import core.di.factory.container.BeanFactoryUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNullElseGet;

public class ClassBeanConstructor implements BeanConstructor {

    private final Class<?> clazz;

    public ClassBeanConstructor(Class<?> clazz) {
        Assert.notNull(clazz, "clazz가 null이어선 안됩니다.");
        this.clazz = clazz;
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

    private Constructor<?> constructor() {
        return requireNonNullElseGet(BeanFactoryUtils.getInjectedConstructor(clazz),
                () -> defaultConstructor(clazz));
    }

    private Constructor<?> defaultConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(clazz.getName(), e);
        }
    }

    @Override
    public Object instantiate(List<Object> args) {
        if (isNotInstanced()) {
            throw new IllegalStateException("생성자가 인스턴스화 될 수 없습니다.");
        }
        return BeanUtils.instantiateClass(constructor(), args.toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassBeanConstructor that = (ClassBeanConstructor) o;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public String toString() {
        return "ClassBeanConstructor{" +
                "clazz=" + clazz +
                '}';
    }
}
