package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static core.di.factory.BeanFactoryUtils.*;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

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
        for (Class<?> bean : preInstanticateBeans) {
            beans.computeIfAbsent(bean, this::getInstance);
        }
    }

    private Object getInstance(Class<?> clazz)  {
        Constructor<?> constructor = getConstructor(clazz);
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        Object[] objects = Arrays.stream(parameterTypes)
                .map(it -> getInstance(BeanFactoryUtils.findConcreteClass(it, preInstanticateBeans)))
                .toArray();

        try {
            return constructor.newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> getConstructor(Class<?> concreteClass) {
        Constructor<?> injectedConstructor = getInjectedConstructor(concreteClass);

        if (Objects.nonNull(injectedConstructor)) {
            return injectedConstructor;
        }

        try {
            return concreteClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Class<?>> getControllerClass() {
        return beans.keySet().stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .collect(Collectors.toUnmodifiableSet());
    }
}
