package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T)beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> preInstantiateBean : preInstantiateBeans) {
            beans.putIfAbsent(preInstantiateBean, fetchBean(preInstantiateBean));
        }
    }

    private Object fetchBean(Class<?> target) {
        if (beans.containsKey(target)) {
            return beans.get(target);
        }

        if (target.isInterface()) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(target, preInstantiateBeans);
            return fetchBean(concreteClass);
        }

        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(target)
            .orElseGet(() -> fetchDefaultConstructor(target));

        return createBean(constructor);
    }

    private Constructor<?> fetchDefaultConstructor(Class<?> target) {
        try {
            return target.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("기본생성자를 찾을 수 없습니다" + target);
        }
    }

    private Object createBean(Constructor<?> constructor) {
        var parameterTypes = constructor.getParameterTypes();
        var parameterCount = constructor.getParameterCount();

        var parameterValues = new Object[parameterCount];

        for (int i = 0; i < parameterTypes.length; i++) {
            parameterValues[i] = fetchBean(parameterTypes[i]);
        }

        try {
            constructor.setAccessible(true);
            return constructor.newInstance(parameterValues);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("bean을 생성할 수 없습니다" + constructor);
        }
    }
}
