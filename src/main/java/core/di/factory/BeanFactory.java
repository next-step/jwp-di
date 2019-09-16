package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.mvc.tobe.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private Map<Class<?>, Object> beans = Maps.newHashMap();
    private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

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

        return instantiateBean(beanDefinition, parameters);
    }

    private Object getParameterObjectByBeanDefinition(Class<?> parameterType) {
        if (beans.containsKey(parameterType)) {
            beans.get(parameterType);
        }

        Object parameter = instantiateBeanDefinition(beanDefinitions.get(parameterType));
        beans.put(parameterType, parameter);
        return parameter;
    }

    private Object instantiateBean(BeanDefinition beanDefinition, Object[] parameters) {
        return beanDefinition.getInstantiateFunction().apply(parameters);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        for (Class<?> clazz : beans.keySet()) {
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
