package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.web.Controller;
import core.di.factory.exception.BeanCurrentlyInCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final BeanDefinitions beanDefinitions;
    private final Set<Class<?>> references = Sets.newHashSet();
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(BeanDefinitions beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        Map<Class<?>, BeanDefinition> beanDefinitions = this.beanDefinitions.getBeanDefinitions();
        Set<Class<?>> classes = beanDefinitions.keySet();
        for (Class<?> clazz : classes) {
            beans.put(clazz, instantiateByClasspath(clazz, classes, beanDefinitions.get(clazz)));
        }
    }

    private Object instantiateByClasspath(Class<?> clazz, Set<Class<?>> preInstantiateBeans, BeanDefinition beanDefinition) {
        Method method = beanDefinition.getMethod();
        if (method == null) {
            return instantiateByClasspath(clazz, preInstantiateBeans);
        }

        return instantiateByMethod(beanDefinition, method);
    }

    private Object instantiateByClasspath(Class<?> clazz, Set<Class<?>> preInstantiateBeans) {
        if (references.contains(clazz)) {
            logger.error(String.format("순환참조가 발생했습니다.[%s]", getReferenceSimpleNames()));
            throw new BeanCurrentlyInCreationException(String.format("순환참조가 발생했습니다.[%s]", getReferenceSimpleNames()));
        }

        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (injectedConstructor == null) {
            return instantiateClass(clazz, preInstantiateBeans);
        }

        try {
            references.add(clazz);
            Object bean = injectedConstructor.newInstance(instantiateConstructor(injectedConstructor, preInstantiateBeans));
            references.remove(clazz);
            beans.put(clazz, bean);
            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error ", e);
        }
        return null;
    }

    private Object instantiateByMethod(BeanDefinition beanDefinition, Method method) {
        try {
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];
            setArguments(parameters, args);
            return method.invoke(beanDefinition.getClazz().newInstance(), args);
        } catch (Exception e) {
            logger.error("Error ", e);
        }
        return null;
    }

    private Object instantiateClass(Class<?> clazz, Set<Class<?>> preInstantiateBeans) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstantiateBeans);
        Object bean = BeanUtils.instantiateClass(concreteClass);
        beans.put(clazz, bean);
        return bean;
    }

    private Object[] instantiateConstructor(Constructor<?> constructor, Set<Class<?>> preInstantiateBeans) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] params = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            params[i] = instantiateByClasspath(parameterTypes[i], preInstantiateBeans);
        }
        return params;
    }

    private void setArguments(Parameter[] parameters, Object[] args) {
        for (int i = 0; i < parameters.length; i++) {
            if (beans.containsKey(parameters[i].getType())) {
                args[i] = beans.get(parameters[i].getType());
            }
        }
    }

    private String getReferenceSimpleNames() {
        return references.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.joining(","));
    }

    public Map<Class<?>, Object> getControllers() {
        return this.beans.entrySet().stream()
                .filter(m -> m.getKey().isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
