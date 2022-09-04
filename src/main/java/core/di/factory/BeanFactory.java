package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import core.annotation.web.Controller;
import core.di.BeanDefinition;
import core.di.BeanDefinitionRegistry;

public class BeanFactory implements BeanDefinitionRegistry {
    private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private Set<Class<?>> preInstantiateBeans = Sets.newHashSet();

    public BeanFactory() {
    }

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @Override
    public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
        beanDefinitions.put(clazz, beanDefinition);
    }

    public void initialize() {
        initiateBeanDefinitions();
        preInstantiateBeans.forEach(this::instantiate);
    }

    private void initiateBeanDefinitions() {
        Set<Class<?>> classes = beanDefinitions.keySet();
        for (Class<?> clazz : classes) {
            getBean(clazz);
        }
    }

    private Object[] getArguments(Class<?>[] parameterTypes) {
        List<Object> arguments = Lists.newArrayList();
        for (Class<?> parameterType : parameterTypes) {
            Object bean = getBean(parameterType);
            if (bean == null) {
                throw new IllegalStateException("Cannot autowire bean : " + parameterType);
            }
            arguments.add(bean);
        }
        return arguments.toArray();
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        Object bean = beans.get(requiredType);
        if (bean != null) {
            return (T) bean;
        }

        BeanDefinition beanDefinition = beanDefinitions.get(requiredType);
        if (beanDefinition.hasMethod()) {
            Method method = beanDefinition.getMethod();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] arguments = getArguments(parameterTypes);
            try {
                Object beanContainingMethod = getBean(method.getDeclaringClass());
                bean = method.invoke(beanContainingMethod, arguments);
                beans.put(requiredType, bean);
                return (T) bean;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(requiredType, beanDefinitions.keySet());
        bean = BeanUtils.instantiateClass(concreteClass);
        beans.put(requiredType, bean);
        return (T) bean;
    }

    private Object instantiate(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        Object instance = createBeanInstance(clazz, constructor);
        beans.put(clazz, instance);
        return instance;
    }

    private Object createBeanInstance(Class<?> clazz, Constructor<?> constructor) {
        if (constructor == null) {
            return BeanUtils.instantiateClass(clazz);
        }
        return instantiateConstructor(constructor);
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> arguments = Lists.newArrayList();
        for (Class<?> parameterType : parameterTypes) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterType, preInstantiateBeans);
            arguments.add(instantiate(concreteClass));
        }
        return BeanUtils.instantiateClass(constructor, arguments.toArray());
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        preInstantiateBeans.stream()
            .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
            .forEach(clazz -> controllers.put(clazz, beans.get(clazz)));
        return controllers;
    }

    public Set<Class<?>> getBeanClasses() {
        return beans.keySet();
    }
}
