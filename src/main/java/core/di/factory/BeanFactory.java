package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

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
        preInstanticateBeans.forEach(preInstanticateBean -> beans.put(preInstanticateBean, createInstance(preInstanticateBean)));
    }

    private Object createInstance(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if (Objects.isNull(constructor)) {
            return instantiateClass(clazz);
        }

        Class<?>[] parameterTypes = constructor.getParameterTypes();

        List<Object> parameters = Arrays.stream(parameterTypes)
                .map(parameterType -> createInstance(parameterType))
                .collect(Collectors.toList());

        return instantiateConstructor(constructor, parameters);
    }

    private Object instantiateConstructor(Constructor<?> constructor, List<Object> parameters) {
        try {
            return constructor.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private Object instantiateClass(Class<?> clazz) {
        return BeanUtils.instantiateClass(BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans));
    }
}
