package core.di.factory;

import com.google.common.collect.ImmutableSet;
import core.annotation.Bean;
import core.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanTypeRegistry {

    private final Map<Class<?>, Object> beanTypes = new HashMap<>();

    public void addType(Class<?> beanClass) {
        if (isConfigurationClass(beanClass)) {
            beanTypes.putAll(getClassBeanAnnotationMethods(beanClass));
        }
        beanTypes.put(beanClass, beanClass);
    }

    private boolean isConfigurationClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(Configuration.class);
    }

    private Map<Class<?>, Method> getClassBeanAnnotationMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .collect(Collectors.toMap(Method::getReturnType, Function.identity()));
    }

    public boolean contains(Class<?> beanClass) {
        boolean contains = beanTypes.containsKey(beanClass);
        if (contains) {
            return true;
        }
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(beanClass, beanTypes.keySet());
        return beanTypes.containsKey(concreteClass);
    }

    public Set<Class<?>> getAllBeanClasses() {
        return ImmutableSet.copyOf(beanTypes.keySet());
    }

    public Object getType(Class<?> beanClass) {
        Object type = beanTypes.get(beanClass);
        if (Objects.nonNull(type)) {
            return type;
        }
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(beanClass, beanTypes.keySet());
        return beanTypes.get(concreteClass);
    }

}
