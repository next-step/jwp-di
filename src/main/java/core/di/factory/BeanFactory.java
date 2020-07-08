package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    //annotation 들인듯?
    private Set<Class<?>> preInstanticateBeans;

    // annotation 별로 가지고 있는 map 인듯
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        if(beans.containsKey(requiredType)) {
            return (T) beans.get(requiredType);
        }
        return (T) makeBean(requiredType);
    }

    //todo : 구현
    public void initialize() {
        // beans 구현해야 하는듯!
        for(Class<?> bean : preInstanticateBeans) {
            getBean(bean);
        }
    }

    private Object makeBean(Class<?> findClass) {
        findClass = BeanFactoryUtils.findConcreteClass(findClass, preInstanticateBeans);

        if(!Optional.ofNullable(BeanFactoryUtils.getInjectedConstructor(findClass)).isPresent()) {
            Object instance = BeanUtils.instantiateClass(findClass);
            this.beans.put(findClass, instance);
            return instance;
        }

        return injectBean(findClass);
    }

    private Object injectBean(Class<?> injectClass) {
        Constructor constructor = BeanFactoryUtils.getInjectedConstructor(injectClass);
        List<Object> constructorValues = new ArrayList<>();

        for(Class<?> findClass : constructor.getParameterTypes()) {

            constructorValues.add(getBean(BeanFactoryUtils.findConcreteClass(findClass, preInstanticateBeans)));
        }

        Object obj = BeanUtils.instantiateClass(constructor, constructorValues.toArray());
        beans.put(injectClass,obj);
        return obj;

    }
}
