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
import java.util.stream.Stream;

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
        Class<?> beanType = BeanFactoryUtils.findConcreteClass(instanceType, preInstantiateBeans);

        if (beans.containsKey(beanType)) {
            return getBean(beanType);
        }

        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(beanType);

        try {
            if (constructor == null) {
                beans.put(beanType, beanType.newInstance());
                return getBean(beanType);
            }

            Object bean = instantiateConstructor(constructor);
            beans.put(beanType, bean);
            return getBean(beanType);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.toString());
        }

        throw new NoSuchElementException("wrong Type " + beanType.toString());
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> parameters = Lists.newArrayList();

        Stream.of(parameterTypes)
                .forEach(parameterType -> {
                    Object parameter = instantiateClass(parameterType);
                    parameters.add(parameter);
                });

        return BeanUtils.instantiateClass(constructor, parameters.toArray());
    }
}
