package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        preInstanticateBeans.forEach(clazz -> beans.put(clazz, beans.computeIfAbsent(clazz, this::getObject)));
    }

    public Map<Class<?>, Object> getControllerTypes() {
        return beans.entrySet()
                .stream()
                .filter(it -> it.getKey().isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Object getObject(Class<?> clazz) {
        Constructor<?> constructor = getConstructor(clazz);
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] objects = Arrays.stream(parameterTypes)
                .map(it -> getObject(BeanFactoryUtils.findConcreteClass(it, preInstanticateBeans)))
                .toArray();

        try {
            return constructor.newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if (Objects.nonNull(injectedConstructor)) {
            return injectedConstructor;
        }

        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
