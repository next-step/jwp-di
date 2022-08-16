package core.di.factory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import core.annotation.web.Controller;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstantiateBeans;
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        preInstantiateBeans.forEach(this::instantiate);
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
}
