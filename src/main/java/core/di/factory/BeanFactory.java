package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static core.di.factory.BeanFactoryUtils.findConcreteClass;
import static core.di.factory.BeanFactoryUtils.getInjectedConstructor;

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
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            if (getBean(preInstanticateBean) != null) {
                continue;
            }
            addBean(preInstanticateBean, instanticateClass(preInstanticateBean));
        }
    }

    private Object instanticateClass(Class clazz) {
        Constructor constructor = getInjectedConstructor(clazz);
        if (constructor != null) {
            return instanticateConstructor(getInjectedConstructor(findConcreteClass(clazz, preInstanticateBeans)));
        }

        return BeanUtils.instantiateClass(findConcreteClass(clazz, preInstanticateBeans));
    }

    private Object instanticateConstructor(Constructor constructor) {
        List<Object> parameters = new ArrayList<>();
        for (Class parameterType : constructor.getParameterTypes()) {
            Object bean = getBeanOrInstanceClass(parameterType);
            parameters.add(bean);
            addBean(parameterType, bean);
        }
        return BeanUtils.instantiateClass(constructor, parameters.toArray());
    }

    private Object getBeanOrInstanceClass(Class parameterType) {
        Object bean = getBean(parameterType);
        if (bean == null) {
            bean = instanticateClass(parameterType);
        }
        return bean;
    }

    private void addBean(Class<?> key, Object value) {
        beans.put(key, value);
    }

}
