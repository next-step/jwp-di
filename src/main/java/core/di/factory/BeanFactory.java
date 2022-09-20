package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import core.annotation.web.Controller;

public class BeanFactory {
    private Set<Class<?>> preInstanticateBeans = new HashSet<>();
    private Map<Class<?>, Method> preInstanticateBeanMethods = new HashMap<>();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
    }

    public void putPreInstanticateBeans(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans.addAll(preInstanticateBeans);
    }

    public void putInstanticateBeanMethods(Map<Class<?>, Method> preInstanticateBeanMethods) {
        this.preInstanticateBeanMethods.putAll(preInstanticateBeanMethods);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        initializeConfigureBeanMethods();
        initializePreInstanticateBeans();
    }

    private void initializePreInstanticateBeans() {
        preInstanticateBeans.stream()
                            .forEach(this::instantiateClass);
    }

    private void initializeConfigureBeanMethods() {
        for (Method method : preInstanticateBeanMethods.values()) {
            instantiateBeanMethod(method);
        }
    }

    private Object instantiateBeanMethod(Method method) {
        Object bean = getBean(method.getReturnType());
        if (Objects.nonNull(bean)) {
            return bean;
        }

        Object instance = BeanUtils.instantiateClass(method.getDeclaringClass());
        Object[] parameters = getBeanMethodParameters(method);
        Object methodBean = ReflectionUtils.invokeMethod(method, instance, parameters);
        beans.put(method.getReturnType(), methodBean);
        return methodBean;
    }

    private Object[] getBeanMethodParameters(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] results = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            results[i] = getBeanMethodParameter(parameterTypes[i]);
        }
        return results;
    }

    private Object getBeanMethodParameter(Class<?> parameterType) {
        Object bean = beans.get(parameterType);
        if (Objects.nonNull(bean)) {
            return bean;
        }
        return instantiateBeanMethod(preInstanticateBeanMethods.get(parameterType));
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
            throw new RuntimeException(clazz.getName() + " : Not Found Constructor");
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
