package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.*;

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
        return (T) makeBean(requiredType);
    }

    public void initialize() {
        for (Class<?> bean : preInstanticateBeans) {
            getBean(bean);
        }
    }

    private Object makeBean(Class findClass) {
        findClass = BeanFactoryUtils.findConcreteClass(findClass, preInstanticateBeans);

        if (!existInjectConstructor(findClass)) {
            Object instance = BeanUtils.instantiateClass(findClass);
            this.beans.put(findClass, instance);
            return instance;
        }

        return injectBean(findClass);

    }

    private Object injectBean(Class injectClass) {
        Constructor constructor = BeanFactoryUtils.getInjectedConstructor(injectClass);
        List<Object> constructorValues = new ArrayList<>();

        for (Class<?> findClass : constructor.getParameterTypes()) {

            constructorValues.add(getBean(BeanFactoryUtils.findConcreteClass(findClass, preInstanticateBeans)));
        }

        Object obj = BeanUtils.instantiateClass(constructor, constructorValues.toArray());
        beans.put(injectClass, obj);
        return obj;

    }

    private boolean existInjectConstructor(Class findClass) {
        return Optional.ofNullable(BeanFactoryUtils.getInjectedConstructor(findClass)).isPresent();
    }
}
