package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class instanceType : preInstantiateBeans) {
            instantiateClass(instanceType);
        }
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        preInstantiateBeans.forEach(type -> {
            if (type.isAnnotationPresent(Controller.class)) {
                controllers.put(type, getBean(type));
            }
        });

        return controllers;
    }

    private Object instantiateClass(Class<?> instanceType) {
        if (beans.containsKey(instanceType)) {
            return getBean(instanceType);
        }

        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(instanceType);

        try {
            if (constructor == null) {
                beans.put(instanceType, instanceType.newInstance());
                return getBean(instanceType);
            }

            Object bean = instantiateConstructor(constructor);
            beans.put(instanceType, bean);
            return getBean(instanceType);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.toString());
            throw new NoSuchElementException("wrong Type " + instanceType.toString());
        }
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> instances = Lists.newArrayList();

        for (Class<?> parameterType : parameterTypes) {
            Class<?> instanceType = BeanFactoryUtils.findConcreteClass(parameterType, preInstantiateBeans);
            Object instance = instantiateClass(instanceType);
            instances.add(instance);
        }

        return BeanUtils.instantiateClass(constructor, instances.toArray());
    }
}
