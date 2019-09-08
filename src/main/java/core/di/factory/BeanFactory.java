package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    private Map<Class<?>, Method> beanMethods = Maps.newHashMap();

    private Class<?> configuration;

    public void registerBeanMethods(Map<Class<?>, Method> beanMethods) {
        this.beanMethods = beanMethods;
    }

    public void registerConfigurationClass(Class<?> configuration) {
        this.configuration = configuration;
    }

    public void registerPreInstantiateBeans(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    public void initializeConfigBeans() {
        initializeConfiguration(configuration);
    }

    public void initializeClassPathBeans() {
        for (Class instanceType : preInstantiateBeans) {
            instantiateClass(instanceType);
        }
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

    private void initializeConfiguration(Class<?> clazz) {
        Collection<Method> methods = beanMethods.values();

        try {
            Object configuration = clazz.newInstance();
            for (Method beanMethod : methods) {
                instantiateBean(configuration, beanMethod);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.toString());
        }
    }

    private Object instantiateBean(Object configuration, Method beanMethod) {
        Class<?> beanType = beanMethod.getReturnType();

        if (beans.containsKey(beanType)) {
            return beans.get(beanType);
        }

        try {
            if (beanMethod.getParameterCount() == 0) {
                beans.put(beanType, beanMethod.invoke(configuration));
                return getBean(beanType);
            }

            List<Parameter> parameters = Arrays.asList(beanMethod.getParameters());
            List<Object> beans = Lists.newArrayList();

            for (Parameter parameter : parameters) {
                Object bean = instantiateBean(configuration, beanMethods.get(parameter.getType()));
                beans.add(bean);
            }

            this.beans.put(beanType, beanMethod.invoke(configuration, beans.toArray()));
            return this.beans.get(beanType);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }
}
