package core.di.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import core.di.factory.definition.BeanDefinition;

public class BeanFactory {

    private final Set<BeanDefinition> preInstantiateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<BeanDefinition> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    public BeanFactory() {
        this(new HashSet<>());
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        if (beans.containsKey(requiredType)) {
            return (T)beans.get(requiredType);
        }

        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(requiredType, beans.keySet());
        if (beans.containsKey(concreteClass)) {
            return (T)beans.get(concreteClass);
        }

        return (T)fetchBean(requiredType);
    }

    public void addBean(BeanDefinition beanDefinition) {
        preInstantiateBeans.add(beanDefinition);
    }

    public void initialize() {
        for (var preInstantiateBean : preInstantiateBeans) {
            fetchBean(preInstantiateBean);
        }
    }

    private Object fetchBean(BeanDefinition target) {
        if (beans.containsKey(target.getBeanClass())) {
            return beans.get(target.getBeanClass());
        }

        if (target.notCreatable()) {
            BeanDefinition concreteClass = BeanFactoryUtils.findConcreteClass(target, preInstantiateBeans);
            return fetchBean(concreteClass);
        }

        return createBean(target);
    }

    private Object fetchBean(Class<?> clazz) {
        var beanDefinition = preInstantiateBeans.stream()
            .filter(it -> it.getBeanClass().equals(clazz) || it.isConcreteClass(clazz))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("정의되지 않은 빈입니다." + clazz));

        return fetchBean(beanDefinition);
    }

    private Object createBean(BeanDefinition target) {
        var parameterTypes = target.getParameterTypes();
        var parameterCount = target.getParameterCount();

        var parameterValues = new Object[parameterCount];

        for (int i = 0; i < parameterTypes.length; i++) {
            parameterValues[i] = fetchBean(parameterTypes[i]);
        }

        try {
            var instance = target.createObject(parameterValues);
            beans.put(target.getBeanClass(), instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("bean을 생성할 수 없습니다" + target);
        }
    }

    public Set<Class<?>> getBeans() {
        return beans.keySet();
    }
}
