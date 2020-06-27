package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import core.di.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstanticateBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        preInstanticateBeans.forEach(beanType -> createBean(new LinkedHashSet<>(), beanType));
    }

    public Map<Class<?>, Object> getBeansByAnnotation(Class<? extends Annotation> annotation) {
        return beans.entrySet()
                .stream()
                .filter(map -> map.getKey().isAnnotationPresent(annotation))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Object createBean(Set<Class<?>> dependency, Class<?> type) {
        if (beans.containsKey(type)) {
            return beans.get(type);
        }

        // change interface to implement class
        if (type.isInterface()) {
            type = getImplementsClass(type);
        }

        checkCircularDependency(dependency, type);

        return newBean(dependency, type);
    }

    private Object newBean(Set<Class<?>> dependency, Class<?> type) {
        dependency.add(type);
        Object instance;

        try {
            // create constructor
            Constructor<?> constructor = getInjectAttachedConstructor(type);

            // get bean of parameters to use as a argument
            Object[] arguments = getArguments(dependency, constructor);

            // create new instance
            constructor.setAccessible(true);
            instance = constructor.newInstance(arguments);

            // add to bean container
            putInContainer(instance, type);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreateException("Fail to create bean of " + type.getName() + " cuz : " + e.getMessage());
        }

        dependency.remove(type);
        return instance;
    }

    private Object[] getArguments(Set<Class<?>> dependency, Constructor<?> constructor) {
        return Arrays.stream(constructor.getParameterTypes())
                .map(parameterType -> createBean(dependency, parameterType))
                .toArray();
    }

    private Class<?> getImplementsClass(Class<?> type) {
        return preInstanticateBeans.stream()
                .filter(type::isAssignableFrom)
                .filter(clazz -> !clazz.isInterface())
                .findFirst()
                .orElseThrow(() -> new NoSuchImplementClassException(type));
    }

    private void putInContainer(Object instance, Class<?> type) {
        if (beans.containsKey(type)) {
            throw new BeanDuplicationException(type);
        }

        beans.put(type, instance);

        Arrays.stream(type.getInterfaces())
                .forEach(aInterface -> putInContainer(instance, aInterface));
    }

    private void checkCircularDependency(Set<Class<?>> dependency, Class<?> type) {
        if (dependency.contains(type)) {
            List<Class<?>> circularDependency = new ArrayList<>(dependency);
            circularDependency.add(type);

            throw new CircularDependencyException(circularDependency);
        }
    }

    private Constructor<?> getInjectAttachedConstructor(Class<?> type) {
        return Arrays.stream(type.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .findFirst()
                .orElseGet(() -> getDefaultConstructor(type));
    }

    private Constructor<?> getDefaultConstructor(Class<?> type) {
        try {
            return type.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new NoDefaultConstructorException(type);
        }
    }
}
