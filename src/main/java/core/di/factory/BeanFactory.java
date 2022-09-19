package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstanticateBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            beans.put(preInstanticateBean, instanticate(preInstanticateBean));
        }
    }

    Object instanticate(Class<?> clazz) {
        try {
            return instantiateClass(clazz);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object instantiateClass(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (injectedConstructor == null) {
            return BeanUtils.instantiateClass(BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans));
        }

        return instantiateConstructor(injectedConstructor);
    }

    private Object instantiateConstructor(Constructor<?> constructor)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            parameters.add(instantiateClass(parameterType));
        }
        return BeanUtils.instantiateClass(constructor, parameters.toArray());
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
