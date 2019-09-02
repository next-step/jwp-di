package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.web.Controller;
import core.mvc.tobe.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.exception.CreateInstanceFailException;
import support.exception.NoSuchDefaultConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private Set<Class<?>> preInstanticateBeans = Sets.newHashSet();
    private Map<Class<?>, Object> beans = Maps.newHashMap();
    private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();


    public void register(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans.addAll(preInstanticateBeans);
        for (Class<?> clazz : this.preInstanticateBeans) {
            beans.put(clazz, instantiateClass(clazz));
        }
    }

    private Object instantiateClass(Class<?> clazz) {
        Constructor<?> constructor = getConstructor(clazz);
        Class<?>[] parameterTypes = getParameterTypes(constructor);
        Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0; i < parameters.length; i++) {
            Object parameter = getParameterObject(parameterTypes[i]);
            parameters[i] = parameter;
        }

        return instantiateConstructor(constructor, parameters);
    }

    private Object getParameterObject(Class<?> parameterType) {
        if (beans.containsKey(parameterType)) {
            return beans.get(parameterType);
        }

        Object parameter = instantiateClass(parameterType);
        beans.put(parameterType, parameter);
        return parameter;
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (Objects.nonNull(constructor)) {
            return constructor;
        }

        return getDefaultConstructor(clazz);
    }

    private Constructor<?> getDefaultConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
            throw new NoSuchDefaultConstructorException();
        }
    }

    private Class<?>[] getParameterTypes(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Class<?>[] concreteClass = new Class[parameterTypes.length];
        for (int i = 0; i < concreteClass.length; i++) {
            concreteClass[i] = BeanFactoryUtils.findConcreteClass(parameterTypes[i], preInstanticateBeans);
        }

        return concreteClass;
    }

    private Object instantiateConstructor(Constructor<?> constructor, Object[] parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage());
            throw new CreateInstanceFailException();
        }
    }

    public void register(Map<Class<?>, BeanDefinition> beanDefinitions) {
        this.beanDefinitions.putAll(beanDefinitions);
        for (Class<?> beanType : this.beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = this.beanDefinitions.get(beanType);
            beans.put(beanType, instantiateBeanDefinition(beanDefinition));
        }
    }

    private Object instantiateBeanDefinition(BeanDefinition beanDefinition) {
        Class<?>[] parameterTypes = beanDefinition.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0; i < parameters.length; i++) {
            Object parameter = getParameterObjectByBeanDefinition(parameterTypes[i]);
            parameters[i] = parameter;
        }

        return invokeBeanCreateMethod(beanDefinition, parameters);
    }

    private Object getParameterObjectByBeanDefinition(Class<?> parameterType) {
        if (beans.containsKey(parameterType)) {
            beans.get(parameterType);
        }

        Object parameter = instantiateBeanDefinition(beanDefinitions.get(parameterType));
        beans.put(parameterType, parameter);
        return parameter;
    }

    private Object invokeBeanCreateMethod(BeanDefinition beanDefinition, Object[] parameters) {
        try {
            Method beanMethod = beanDefinition.getBeanCreateMethod();
            return beanMethod.invoke(beanDefinition.getConfigurationObject(), parameters);
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage());
            throw new CreateInstanceFailException();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        for (Class<?> clazz : preInstanticateBeans) {
            addController(controllers, clazz);
        }

        return controllers;
    }

    private void addController(Map<Class<?>, Object> controllers, Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Controller.class)) {
            return;
        }

        controllers.put(clazz, getBean(clazz));
    }
}
