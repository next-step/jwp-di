package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import core.exception.NoSuchBeanConstructorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstanticateBeans = new HashSet<>();
    private final Map<Method, Class<?>> preInstanticateMethods = new HashMap<>();

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
    }

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans.addAll(preInstanticateBeans);
    }

    public void register(Class<?> clazz, Method method) {
        preInstanticateMethods.put(method, clazz);
    }

    public void register(Class<?> clazz) {
        preInstanticateBeans.add(clazz);
    }

    public void register(Set<Class<?>> clazzSet) {
        preInstanticateBeans.addAll(clazzSet);
    }

    public void addBean(Class<?> bean) {
        Object instance = createInstance(bean);
        beans.put(instance.getClass(), instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        preInstanticateMethods.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getParameterCount()))
                .forEach(entry -> {
                    Object instance = createInstance(getBean(entry.getValue()), entry.getKey());
                    beans.put(entry.getKey().getReturnType(), instance);
                });

        preInstanticateBeans.stream().sorted(Comparator.comparingInt(clazz -> {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
            Constructor<?> constructor = findConstructor(concreteClass);
            return constructor.getParameterCount();
        })).forEach(clazz -> {
            Object instance = createInstance(clazz);
            beans.put(clazz, instance);
        });
    }

    private Object createInstance(Object instance, Method method) {
        List<Object> parameters = new ArrayList<>();

        for (Class<?> typeClass : method.getParameterTypes()) {
            parameters.add(getParameterByClass(typeClass));
        }

        try {
            return method.invoke(instance, parameters.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Bean 생성에 실패했습니다. [target:{}, cause:{}]", instance.getClass().getName(), e.getCause());
            throw new RuntimeException(e);
        }
    }

    private Object createInstance(Class<?> clazz) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
        Constructor<?> constructor = findConstructor(concreteClass);
        List<Object> parameters = new ArrayList<>();

        for (Class<?> typeClass : constructor.getParameterTypes()) {
            parameters.add(getParameterByClass(typeClass));
        }

        try {
            return constructor.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Bean 생성에 실패했습니다. [target:{}, cause:{}]", concreteClass.getName(), e.getCause());
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> findConstructor(Class<?> concreteClass) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);

        if (Objects.nonNull(constructor)) {
            return constructor;
        }

        Constructor<?>[] constructors = concreteClass.getConstructors();

        if (constructors.length > 0) {
            return constructors[0];
        }

        throw new NoSuchBeanConstructorException(concreteClass);
    }

    private Object getParameterByClass(Class<?> typeClass) {
        Object bean = getBean(typeClass);

        if (Objects.nonNull(bean)) {
            return bean;
        }

        return createInstance(typeClass);
    }

    public Set<Class<?>> getControllers() {
        return beans.keySet().stream()
                .filter(key -> key.isAnnotationPresent(Controller.class))
                .collect(Collectors.toSet());
    }
}
