package core.di.factory;

import core.di.factory.exception.NoSuchDefaultConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ClassPathBeanDefinition implements BeanDefinition {

    private Class<?> clazz;

    public ClassPathBeanDefinition(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Method getMethod() {
        return null;
    }

    @Override
    public Class<?>[] parameterTypes() {
        return getConstructor().getParameterTypes();
    }

    @Override
    public Object instantiate(Object[] objects) {
        Constructor<?> constructor = getConstructor();
        try {
            return constructor.newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new NoSuchDefaultConstructorException(e);
        }
    }

    private Constructor<?> getConstructor() {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        return Objects.requireNonNullElseGet(injectedConstructor, () -> getDefaultConstructor(clazz));

    }

    private Constructor<?> getDefaultConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
