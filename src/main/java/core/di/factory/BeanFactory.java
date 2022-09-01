package core.di.factory;

import core.di.factory.constructor.BeanConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class BeanFactory {

    private final Map<Class<?>, BeanConstructor> beanConstructors;
    private final Map<Class<?>, Object> beans = new HashMap<>();

    private BeanFactory(Collection<BeanConstructor> beanConstructors) {
        Assert.notNull(beanConstructors, "beanConstructors must not be null");
        this.beanConstructors = beanConstructors.stream()
                .collect(Collectors.toUnmodifiableMap(BeanConstructor::type, constructor -> constructor));
    }

    public static BeanFactory from(Collection<BeanConstructor> beanConstructors) {
        return new BeanFactory(beanConstructors);
    }

    public void initialize() {
        beanConstructors.values().forEach(this::instancedSingleton);
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

    private Object instancedSingleton(BeanConstructor constructor) {
        if (beans.containsKey(constructor.type())) {
            return beans.get(constructor.type());
        }
        Object instance = instantiateClass(constructor);
        beans.put(constructor.type(), instance);
        return instance;
    }

    private Object instantiateClass(BeanConstructor beanConstructor) {
        if (beanConstructor.isNotInstanced()) {
            return instancedSingleton(beanConstructors.get(subType(beanConstructor.type())));
        }
        return beanConstructor.instantiate(arguments(beanConstructor));
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
                .map(this::instancedSingleton)
                .collect(Collectors.toList());
    }
}
