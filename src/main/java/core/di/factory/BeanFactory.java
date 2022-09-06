package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
        preInstanticateBeans
                .forEach(preInstanticateBean -> beans.put(preInstanticateBean, instanticateBean(preInstanticateBean)));
    }

    public Object instanticateBean(Class<?> clazz) {
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        final Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        if (constructor == null) {
            return BeanUtils.instantiateClass(clazz);
        }

        try {
            return constructor.newInstance(getParameterValues(constructor));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("BeanFactory 초기화를 실패하였습니다.", e);
        }
    }

    private Object[] getParameterValues(Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] parameterValues = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            final Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterType, preInstanticateBeans);
            final Object instance = getInstance(concreteClass);
            parameterValues[i] = instance;
        }
        return parameterValues;
    }

    private Object getInstance(Class<?> concreteClass) {
        if (beans.containsKey(concreteClass)) {
            return beans.get(concreteClass);
        }
        return instanticateBean(concreteClass);
    }
}
