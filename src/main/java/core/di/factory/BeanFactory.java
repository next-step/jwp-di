package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.di.BeanDefinition;
import core.di.exception.BeanInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private Map<Class<?>, Object> beans = Maps.newHashMap();


    public BeanFactory() {
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (BeanDefinition beanDefinition : beanDefinitions.values()) {
            initializeBean(beanDefinition);
        }
        logger.debug("beans: {}", beans);
    }

    private void initializeBean(BeanDefinition beanDefinition) {
        try {
            registerBean(beanDefinition);
        } catch (ReflectiveOperationException e) {
            logger.error("bean instantiate exception : type = {}", beanDefinition.getType().getTypeName(), e);
            throw new BeanInstantiationException(beanDefinition.getType(), e);
        }
    }

    private Object registerBean(BeanDefinition beanDefinition) throws ReflectiveOperationException {

        Class<?> type = beanDefinition.getType();
        if (beans.containsKey(type)) {
            return beans.get(type);
        }

        if (beanDefinition.isFactoryBean()) {
            Object bean = getFactoryBean(beanDefinition);
            beans.put(type, bean);
            return bean;
        }

        Constructor<?> injectedConstructor = getInjectedConstructor(type);
        if (injectedConstructor == null) {
            Object bean = type.newInstance();
            beans.put(type, bean);
            return bean;
        }

        Object bean = instantiateConstructor(injectedConstructor);
        beans.put(type, bean);
        return bean;
    }

    private Object getFactoryBean(BeanDefinition beanDefinition) throws ReflectiveOperationException {
        Method factoryMethod = beanDefinition.getFactoryMethod();
        Object[] args = getDependencies(factoryMethod.getParameters());
        return factoryMethod.invoke(beanDefinition.getFactory(), args);
    }

    private Object instantiateConstructor(Constructor<?> constructor) throws ReflectiveOperationException {
        Parameter[] parameters = constructor.getParameters();
        Object[] dependencies = getDependencies(parameters);
        return constructor.newInstance(dependencies);
    }

    private Object[] getDependencies(Parameter[] parameters) throws ReflectiveOperationException {
        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> argumentType = findConcreteClass(parameters[i].getType(), beanDefinitions.keySet());
            Object argument = getDependentBean(argumentType);
            arguments[i] = argument;
        }
        return arguments;
    }

    private Object getDependentBean(Class<?> dependentBeanType) throws ReflectiveOperationException {
        if (beans.containsKey(dependentBeanType)) {
            return getBean(dependentBeanType);
        }
        return registerBean(beanDefinitions.get(dependentBeanType));
    }

    public Map<Class<?>, Object> getControllers() {
        return beans.entrySet().stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void registerBeanDefinition(Collection<BeanDefinition> beanDefinitions) {
        this.beanDefinitions.putAll(beanDefinitions.stream().collect(Collectors.toMap(BeanDefinition::getType, bd -> bd)));
    }
}
