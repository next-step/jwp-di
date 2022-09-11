package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstanticateBeans;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
        this.preInstanticateBeans = new HashSet<>();
    }

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        if (beans.containsKey(requiredType)) {
            return (T) beans.get(requiredType);
        }
        return (T) createInstance(requiredType);
    }

    public void initialize() {
        preInstanticateBeans.forEach(this::createInstance);
    }

    public void addPreInstantiateBeans(Set<Class<?>> beans) {
        this.preInstanticateBeans.addAll(beans);
    }

    public void addBean(Class<?> clazz, Object bean) {
        beans.put(clazz, bean);
    }

    private Object createInstance(Class<?> clazz) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
        Constructor<?> constructor = constructor(concreteClass);

        try {
            return constructor.newInstance(parameters(constructor).toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Bean 생성에 실패했습니다. [class: {}, cause: {}]", concreteClass.getName(), e.getCause());
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> constructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);
            if (constructor == null) {
                return clazz.getConstructor();
            }
            return constructor;

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Object> parameters(Constructor<?> constructor) {
        return Arrays.stream(constructor.getParameterTypes())
                .map(typeClass -> getBean(BeanFactoryUtils.findConcreteClass(typeClass, preInstanticateBeans)))
                .collect(Collectors.toList());
    }
}
