package core.di.factory;

import core.annotation.Bean;
import core.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanMethodRegistry {

    private final Map<Class<?>, Method> beanMethods = new HashMap<>();

    public boolean contains(Class<?> returnType) {
        return beanMethods.containsKey(returnType);
    }

    public Method getMethod(Class<?> returnType) {
        return beanMethods.get(returnType);
    }

    public Collection<Method> getAllMethods() {
        return beanMethods.values();
    }

    public Set<Class<?>> addAllConfigurationBeanMethods(Class<?> beanClass) {
        if (!isConfigurationClass(beanClass)) {
            return Collections.emptySet();
        }
        Map<Class<?>, Method> classBeanAnnotationMethods = getClassBeanAnnotationMethods(beanClass);
        beanMethods.putAll(classBeanAnnotationMethods);
        return classBeanAnnotationMethods.keySet();
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

}
