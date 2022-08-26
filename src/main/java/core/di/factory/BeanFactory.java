package core.di.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.google.common.collect.Maps;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        if (beans.containsKey(requiredType)) {
            return (T) beans.get(requiredType);
        }
        return (T) instantiateClass(requiredType);
    }

    public void initialize() {
        for (Class<?> bean : preInstanticateBeans) {
            instantiateClass(bean);
        }
    }

    private Object instantiateClass(Class<?> clazz) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
        Constructor<?> constructor = getConstructor(concreteClass);
        Object[] parameters = getParameters(constructor);
        Object instance = BeanUtils.instantiateClass(constructor, parameters);

        beans.put(clazz, instance);
        return instance;
    }

    private Object[] getParameters(Constructor<?> constructor) {
        List<Object> constructorValues = new ArrayList<>();
        for (Class<?> findClass : constructor.getParameterTypes()) {
            constructorValues.add(getBean(BeanFactoryUtils.findConcreteClass(findClass, preInstanticateBeans)));
        }
        return constructorValues.toArray();
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
            if (constructor == null) {
                return clazz.getConstructor();
            }
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Not Found Constructor");
        }
    }
}
