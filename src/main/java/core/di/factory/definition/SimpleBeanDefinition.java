package core.di.factory.definition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import core.annotation.Inject;

public class SimpleBeanDefinition implements BeanDefinition {

    private final Class<?> clazz;
    private final Constructor<?> constructor;

    public SimpleBeanDefinition(Class<?> clazz) {
        this.clazz = clazz;
        this.constructor = Arrays.stream(clazz.getDeclaredConstructors())
            .filter(it -> it.isAnnotationPresent(Inject.class))
            .findAny()
            .orElseGet(() -> getDefaultConstructor(clazz));
    }

    private Constructor<?> getDefaultConstructor(Class<?> clazz) {
        if (clazz.isInterface()) {
            return null;
        }

        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("빈의 기본생성자가 없습니다." + clazz);
        }
    }

    @Override
    public Class<?> getBeanClass() {
        return clazz;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return constructor.getParameterTypes();
    }

    @Override
    public Object createObject(Object[] parameters) throws
        InstantiationException,
        IllegalAccessException,
        InvocationTargetException {
        return constructor.newInstance(parameters);
    }

    @Override
    public boolean notCreatable() {
        return clazz.isInterface();
    }

    @Override
    public int getParameterCount() {
        return constructor.getParameterCount();
    }

    @Override
    public String toString() {
        return "SimpleBeanDefinition{" +
            "clazz=" + clazz +
            ", constructor=" + constructor +
            '}';
    }
}
