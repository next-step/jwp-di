package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;


import java.lang.reflect.Constructor;
import java.util.Map;
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
        for (Class<?> beanClazz : this.preInstanticateBeans) {
            Object bean = instantiateBean(beanClazz);
            beans.put(beanClazz, bean);
            logger.debug("bean registration complete : {}", beanClazz.getName());
        }
    }

    private Object instantiateBean(Class<?> beanClazz) {
        Object bean = getBean(beanClazz);
        if (bean != null) {
            return bean;
        }

        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(beanClazz);
        if (injectedConstructor == null) {
            return BeanUtils.instantiateClass(beanClazz);
        }

        return instantiateBeanWithArgs(injectedConstructor);
    }

    private Object instantiateBeanWithArgs(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterTypes[i], this.preInstanticateBeans);
            Object bean = beans.get(concreteClass);
            if (bean == null) {
                bean = instantiateBean(concreteClass);
            }
            args[i] = bean;
        }
        return BeanUtils.instantiateClass(constructor, args);
    }

}
