package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> beanClass : preInstanticateBeans) {
            instantiate(beanClass);
        }
    }

    private Object instantiate(Class<?> beanClass) {
        Object instance = newInstance(beanClass);
        this.beans.put(beanClass, instance);
        return instance;
    }

    private Object newInstance(Class<?> beanClass) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(beanClass, preInstanticateBeans);
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);
        if (Objects.isNull(constructor)) {
            return BeanUtils.instantiateClass(concreteClass);
        }

        return BeanUtils.instantiateClass(constructor, getParameterInstances(constructor));
    }

    private Object[] getParameterInstances(Constructor<?> constructor) {
        Class<?>[] parameterClasses = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterClasses.length];
        for (int i = 0; i < parameterClasses.length; i++) {
            parameters[i] = getParameterInstance(parameterClasses[i]);
        }
        return parameters;
    }

    private Object getParameterInstance(Class<?> parameterClass) {
        Object bean = getBean(parameterClass);
        if (Objects.nonNull(bean)) {
            return bean;
        }
        return instantiate(parameterClass);
    }

}
