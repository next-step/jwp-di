package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import core.annotation.web.Controller;

public class BeanFactory {
    private Set<Class<?>> preInstanticateBeans = new HashSet<>();
    Map<Method, Object> configureBeanMethod = new HashMap<>();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
    }

    public void putPreInstanticateBeans(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans.addAll(preInstanticateBeans);
    }

    public void putConfigureBeans(Map<Method, Object> configureBeanMethod) {
        this.configureBeanMethod.putAll(configureBeanMethod);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        initializePreInstanticateBeans();
        initializeConfigureBeans();
    }

    private void initializePreInstanticateBeans() {
        preInstanticateBeans.stream()
                            .forEach(this::instantiateClass);
    }

    private void initializeConfigureBeans() {
        for(Method method : configureBeanMethod.keySet()) {
            addConfigurationBean(method, configureBeanMethod.get(method));
        }
    }

    private void addConfigurationBean(Method method, Object instance) {
        try {
            Class<?> returnType = method.getReturnType();
            if (beans.containsKey(returnType)) {
                return;
            }
            Parameter[] parameters = method.getParameters();
            List<Object> objects = new ArrayList<>();
            for (Parameter parameter : parameters) {
                objects.add(getBean(parameter.getType()));
            }
            Object obj = method.invoke(instance, objects.toArray());

            beans.put(returnType, obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Object instantiateClass(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> constructor = getConstructor(clazz);
        Object instance = instantiateConstructor(constructor);
        beans.put(clazz, instance);
        return instance;
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        List<Object> args = Lists.newArrayList();
        for (Class<?> clazz : constructor.getParameterTypes()) {
            args.add(instantiateClass(BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans)));
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
            if (constructor == null) {
                return clazz.getConstructor();
            }
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Not Found Constructor");
        }
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        for (Class<?> clazz : preInstanticateBeans) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.put(clazz, beans.get(clazz));
            }
        }
        return controllers;
    }
}
