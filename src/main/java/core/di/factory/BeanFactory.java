package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        preInstanticateBeans
                .forEach(preInstanticateBean -> beans.put(preInstanticateBean, instanticateBean(preInstanticateBean)));

        if (logger.isTraceEnabled()) {
            beans.values().forEach(value -> logger.trace(value.toString()));
        }
    }

    private Object instanticateBean(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }
        return instanticate(clazz);
    }

    private Object instanticate(Class<?> clazz) {
        final Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (constructor == null) {
            return BeanUtils.instantiateClass(clazz);
        }
        return BeanUtils.instantiateClass(constructor, getArgs(constructor));
    }

    private Object[] getArgs(Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final List<Object> args = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            final Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterType, preInstanticateBeans);
            final Object instance = getInstance(concreteClass);
            args.add(instance);
            beans.put(concreteClass, instance);
        }
        return args.toArray();
    }

    private Object getInstance(Class<?> concreteClass) {
        if (beans.containsKey(concreteClass)) {
            return beans.get(concreteClass);
        }
        return instanticateBean(concreteClass);
    }
}
