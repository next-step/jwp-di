package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

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
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            beans.put(preInstantiateBean, instantiateClass(preInstantiateBean));
        }
    }

    public Set<Class<?>> getControllerBeans() {
        return preInstantiateBeans.stream()
                .filter(bean -> bean.isAnnotationPresent(Controller.class))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Object instantiateClass(Class<?> clazz) {
        logger.debug("InstantiateClass : {}", clazz);
        if (beans.containsKey(clazz)) {
            logger.debug("Cached class : {}", clazz);
            return beans.get(clazz);
        }
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstantiateBeans);
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);
        if (injectedConstructor == null) {
            logger.debug("injectedConstructor not exist. Default Constructor : {}", concreteClass);
            return initiateByDefaultConstructor(concreteClass);
        }
        return instantiateInjectedConstructor(injectedConstructor);
    }

    private Object initiateByDefaultConstructor(Class<?> concreteClass) {
        try {
            return concreteClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private Object instantiateInjectedConstructor(Constructor<?> injectedConstructor) {
        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = instantiateClass(parameterTypes[i]);
        }
        return getInjectedConstructorInstance(injectedConstructor, parameters);
    }

    private Object getInjectedConstructorInstance(Constructor<?> injectedConstructor, Object[] parameters) {
        try {
            return injectedConstructor.newInstance(parameters);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
