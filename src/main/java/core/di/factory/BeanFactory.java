package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import core.annotation.Inject;

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
            Class<?> beanClass = findRootClass(preInstantiateBean);

            beans.putIfAbsent(beanClass, fetchBean(preInstantiateBean));
        }
    }

    private Class<?> findRootClass(Class<?> preInstantiateBean) {
        return Arrays.stream(preInstantiateBean.getInterfaces())
            .findAny()
            .orElse(preInstantiateBean);
    }

    private Object fetchBean(Class<?> target) {
        if (beans.containsKey(target)) {
            return beans.get(target);
        }

        var maybeConstructor = Arrays.stream(target.getDeclaredConstructors())
            .filter(it -> it.isAnnotationPresent(Inject.class))
            .findAny();

        if (maybeConstructor.isEmpty()) {
            return createRootBean(target);
        }

        Constructor<?> constructor = maybeConstructor.get();
        return createParameterBean(constructor);
    }

    private Object createParameterBean(Constructor<?> constructor) {
        var parameterTypes = constructor.getParameterTypes();
        var parameterCount = constructor.getParameterCount();

        var parameterValues = new Object[parameterCount];

        for (int i = 0; i < parameterTypes.length; i++) {
            parameterValues[i] = fetchBean(parameterTypes[i]);
        }

        try {
            return constructor.newInstance(parameterValues);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("bean을 생성할 수 없습니다" + constructor);
        }
    }

    private Object createRootBean(Class<?> target) {
        if (target.isInterface()) {
            var reflections = new Reflections(target);
            Class<?> subType = reflections.getSubTypesOf((Class<Object>)target).stream()
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("인터페이스의 구현체가 없습니다"));
            return fetchBean(subType);
        }

        try {
            var declaredConstructor = target.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Bean을 생성할 수 없습니다 (no-args)" + target);
        }
    }
}
