package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.di.factory.exception.BeanCurrentlyInCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiateBeans;
    private Set<Class<?>> references = new HashSet<>();
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            beans.put(preInstantiateBean, instantiate(preInstantiateBean));
        }
    }

    private Object instantiate(Class<?> clazz) {
        if (references.contains(clazz)) {
            throw new BeanCurrentlyInCreationException("순환참조가 발생했습니다.");
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

    public Set<Class<?>> getControllers() {
        return beans.keySet().stream()
                .filter(c -> c.isAnnotationPresent(Controller.class))
                .collect(Collectors.toSet());
    }

}
