package core.di.factory.container;

import com.google.common.collect.Maps;
import core.di.factory.constructor.BeanConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Map<Class<?>, BeanConstructor> beanConstructors;
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Collection<BeanConstructor> beanConstructors) {
        Assert.notNull(beanConstructors, "beanConstructors가 null이어선 안됩니다.");
        this.beanConstructors = beanConstructors.stream()
                .collect(Collectors.toUnmodifiableMap(BeanConstructor::type, constructor -> constructor));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Collection<Object> beansAnnotatedWith(Class<? extends Annotation> annotation) {
        return beans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotation))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public void initialize() {
        beanConstructors.values().forEach(this::getInstantiateSingletons);
    }

    private Object getInstantiateSingletons(BeanConstructor constructor) {
        Assert.notNull(constructor, "constructor가 null이어선 안됩니다.");
        if (beans.containsKey(constructor.type())) {
            return beans.get(constructor.type());
        }
        Object instance = instantiateClass(constructor);
        beans.put(constructor.type(), instance);
        return instance;
    }

    private Object instantiateClass(BeanConstructor constructor) {
        if (constructor.isNotInstanced()) {
            return getInstantiateSingletons(beanConstructors.get(subType(constructor.type())));
        }
        return constructor.instantiate(arguments(constructor));
    }

    private Class<?> subType(Class<?> clazz) {
        return beanConstructors.keySet()
                .stream()
                .filter(type -> !type.equals(clazz) && clazz.isAssignableFrom(type))
                .findAny()
                .orElseThrow(() -> new NoSuchBeanDefinitionException(clazz));
    }

    private List<Object> arguments(BeanConstructor constructor) {
        return constructor.parameterTypes()
                .stream()
                .map(beanConstructors::get)
                .map(this::getInstantiateSingletons)
                .collect(Collectors.toList());
    }
}
