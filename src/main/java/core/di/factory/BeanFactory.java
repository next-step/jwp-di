package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    private Object configInstance;

    private PreInstanceBeanHandler pibh;

    public void registerConfigurationClass(Class<?> configuration) {
        try {
            this.configInstance = configuration.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerPreInstanceBeanHandler(PreInstanceBeanHandler pibh) {
        this.pibh = pibh;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        Set<Class<?>> preInstantiateBeans = pibh.getClassPathBeans();

        preInstantiateBeans.forEach(type -> {
            if (type.isAnnotationPresent(Controller.class)) {
                controllers.put(type, getBean(type));
            }
        });

        return controllers;
    }

    public void initializeBeans() {
        Set<Class<?>> preBeans = pibh.getPreInstanceBeans();

        for (Class instanceType : preBeans) {
            instantiateClass(instanceType);
        }
    }

    private Object instantiateClass(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return getBean(clazz);
        }

        if (pibh.isConfigurationBean(clazz)) {
            Object bean = instantiateBean(pibh.getMethod(clazz));
            beans.put(clazz, bean);
            return getBean(clazz);
        }

        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        try {
            if (constructor == null) {
                beans.put(clazz, clazz.newInstance());
                return getBean(clazz);
            }

            Object bean = instantiateConstructor(constructor);
            beans.put(clazz, bean);
            return getBean(clazz);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    private Object instantiateBean(Method beanMethod) {
        Class<?>[] parameterTypes = beanMethod.getParameterTypes();
        List<Object> parameters = instantiateParameters(parameterTypes);

        try {
            return beanMethod.invoke(configInstance, parameters.toArray());
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("beanMethod : {} | error : {}", beanMethod.getName(), e.toString());
            throw new RuntimeException(e);
        }
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> instances = instantiateParameters(parameterTypes);

        return BeanUtils.instantiateClass(constructor, instances.toArray());
    }

    private List<Object> instantiateParameters(Class<?>[] parameterTypes) {
        List<Object> parameters = Lists.newArrayList();

        for (Class<?> parameterType : parameterTypes) {
            parameterType = getConcreteClass(parameterType);

            Object bean = instantiateClass(parameterType);
            parameters.add(bean);
        }

        return parameters;
    }

    private Class<?> getConcreteClass(Class<?> parameterType) {
        if (pibh.isConfigurationBean(parameterType)) {
            return parameterType;
        }
        return BeanFactoryUtils.findConcreteClass(parameterType, pibh.getClassPathBeans());
    }
}