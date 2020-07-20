package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.Bean;
import core.annotation.web.Controller;
import core.di.factory.exception.BeanCurrentlyInCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstantiateBeans = Sets.newHashSet();
    private final Set<Class<?>> configurations = Sets.newHashSet();
    private final Set<Class<?>> references = Sets.newHashSet();
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() { }

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans.addAll(preInstantiateBeans);
    }

    public void addAllPreInstantiateBeans(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans.addAll(preInstantiateBeans);
    }

    public void addAllConfigurations(List<Class<?>> configurations) {
        this.configurations.addAll(configurations);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            beans.put(preInstantiateBean, instantiate(preInstantiateBean));
        }

        for (Class<?> configuration : configurations) {
            registerConfiguration(configuration);
        }
    }

    private Object instantiate(Class<?> clazz) {
        if (references.contains(clazz)) {
            logger.error(String.format("순환참조가 발생했습니다.[%s]", getReferenceSimpleNames()));
            throw new BeanCurrentlyInCreationException(String.format("순환참조가 발생했습니다.[%s]", getReferenceSimpleNames()));
        }

        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (injectedConstructor == null) {
            return instantiateClass(clazz);
        }

        try {
            references.add(clazz);
            Object bean = injectedConstructor.newInstance(instantiateConstructor(injectedConstructor));
            references.remove(clazz);
            beans.put(clazz, bean);
            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error ", e);
        }
        return null;
    }

    private void registerConfiguration(Class<?> clazz) {
        try {
            Object instance = clazz.newInstance();
            List<Method> beanMethods = getBeans(clazz);
            for (Method beanMethod : beanMethods) {
                Parameter[] parameters = beanMethod.getParameters();
                Object[] args = new Object[parameters.length];
                setArguments(parameters, args);
                Object result = beanMethod.invoke(instance, args);
                beans.put(beanMethod.getReturnType(), result);
            }

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error ", e);
        }
    }

    private Object instantiateClass(Class<?> clazz) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstantiateBeans);
        Object bean = BeanUtils.instantiateClass(concreteClass);
        beans.put(clazz, bean);
        return bean;
    }

    private Object[] instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] params = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            params[i] = instantiate(parameterTypes[i]);
        }
        return params;
    }

    private List<Method> getBeans(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
                .filter(m -> m.isAnnotationPresent(Bean.class))
                .sorted(Comparator.comparing(m -> m.getParameters().length))
                .collect(Collectors.toList());
    }

    private void setArguments(Parameter[] parameters, Object[] args) {
        for (int i = 0; i < parameters.length; i++) {
            if (beans.containsKey(parameters[i].getType())) {
                args[i] = beans.get(parameters[i].getType());
            }
        }
    }

    public Map<Class<?>, Object> getControllers() {
        return this.beans.entrySet().stream()
                .filter(m -> m.getKey().isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    private String getReferenceSimpleNames() {
        return references.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.joining(","));
    }

}
