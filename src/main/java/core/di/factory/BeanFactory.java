package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Bean;
import core.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Map<Class<?>, Method> beanMethods = getBeanMethods();
        for (Method method : beanMethods.values()) {
            instantiateBeanMethod(beanMethods, method);
        }

        for (Class<?> beanClass : preInstanticateBeans) {
            instantiateComponents(beanClass);
        }
    }

    private Map<Class<?>, Method> getBeanMethods() {
        return getConfigurationClasses().stream()
                .map(Class::getMethods)
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .collect(Collectors.toMap(Method::getReturnType, Function.identity()));
    }

    private Set<Class<?>> getConfigurationClasses() {
        return preInstanticateBeans.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Configuration.class))
                .collect(Collectors.toSet());
    }

    private Object instantiateBeanMethod(Map<Class<?>, Method> beanMethods, Method method) {
        Object instance = BeanUtils.instantiateClass(method.getDeclaringClass());
        Object[] parameters = getBeanMethodParameterInstances(method, beanMethods);
        Object bean = ReflectionUtils.invokeMethod(method, instance, parameters);
        beans.put(method.getReturnType(), bean);
        return bean;
    }

    private Object[] getBeanMethodParameterInstances(Method method, Map<Class<?>, Method> beanMethods) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] results = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            results[i] = getParameterInstance(beanMethods, parameterTypes[i]);
        }
        return results;
    }

    private Object getParameterInstance(Map<Class<?>, Method> beanMethods, Class<?> parameterType) {
        Object bean = beans.get(parameterType);
        if (Objects.nonNull(bean)) {
            return bean;
        }
        return instantiateBeanMethod(beanMethods, beanMethods.get(parameterType));
    }

    private Object instantiateComponents(Class<?> beanClass) {
        Object instance = newInstance(beanClass);
        this.beans.put(beanClass, instance);
        return instance;
    }

    private Object newInstance(Class<?> beanClass) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(beanClass, preInstanticateBeans);
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);
        if (Objects.isNull(constructor)) {
            return BeanUtils.instantiateClass(concreteClass);
        }

        return BeanUtils.instantiateClass(constructor, getParameterInstances(constructor));
    }

    private Object[] getParameterInstances(Constructor<?> constructor) {
        Class<?>[] parameterClasses = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterClasses.length];
        for (int i = 0; i < parameterClasses.length; i++) {
            parameters[i] = getParameterInstance(parameterClasses[i]);
        }
        return parameters;
    }

    private Object getParameterInstance(Class<?> parameterClass) {
        Object bean = getBean(parameterClass);
        if (Objects.nonNull(bean)) {
            return bean;
        }
        return instantiateComponents(parameterClass);
    }

}
