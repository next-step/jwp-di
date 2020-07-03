package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    public void initialize() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            if (beans.containsKey(preInstanticateBean)) {
                continue;
            }
            beans.put(preInstanticateBean, getInstance(preInstanticateBean));
        }
    }

    private Object getInstance(Class<?> classType) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (beans.containsKey(classType)) {
            return beans.get(classType);
        }

        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(classType);
        if (Objects.nonNull(constructor)) {
            beans.put(classType, getConstructorNewInstance(constructor));
            return beans.get(classType);
        }

        Class<?> clazz = BeanFactoryUtils.findConcreteClass(classType, preInstanticateBeans);
        return BeanUtils.instantiateClass(clazz);
    }

    private Object getConstructorNewInstance(Constructor<?> constructor) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (constructor.getParameterCount() > 0) {
            return constructor.newInstance(getInitArgs(constructor));
        }
        return constructor.newInstance();
    }

    private Object[] getInitArgs(Constructor<?> constructor) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object[] initArgs = new Object[constructor.getParameterCount()];
        for (int i = 0; i < constructor.getParameterTypes().length; i++) {
            initArgs[i] = getInstance(constructor.getParameterTypes()[i]);
        }
        return initArgs;
    }
}
