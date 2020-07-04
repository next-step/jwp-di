package core.di.beans.definition;

import com.google.common.collect.Sets;
import core.di.factory.BeanFactoryUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class DefaultBeanDefinition implements BeanDefinition {
    protected Class<?> type;
    protected Constructor<?> constructor;
    protected Set<Field> fields;

    public DefaultBeanDefinition(Class<?> type) {
        this.type = type;
        this.constructor = getInjectedConstructor(type);
        this.fields = getInjectedFields(type);
    }

    private Constructor<?> getInjectedConstructor(Class<?> type) {
        return BeanFactoryUtils.getInjectedConstructor(type);
    }

    private Set<Field> getInjectedFields(Class<?> type) {
        if (Objects.nonNull(constructor)) {
            return Sets.newHashSet();
        }

        return getInjectedFields(type.getFields(), getInjectedPropertyTypes(type));
    }

    private Set<Field> getInjectedFields(Field[] fields, Set<Class<?>> injectedPropertyTypes) {
        return Arrays.stream(fields)
            .filter(injectedPropertyTypes::contains)
            .collect(toSet());
    }

    private static Set<Class<?>> getInjectedPropertyTypes(Class<?> clazz) {
        Set<Class<?>> injectedProperties = Sets.newHashSet();
        injectedProperties.addAll(getSetterProperties(BeanFactoryUtils.getInjectedMethods(clazz)));
        injectedProperties.addAll(getFieldProperties(BeanFactoryUtils.getInjectedFields(clazz)));
        return injectedProperties;
    }

    private static Set<? extends Class<?>> getSetterProperties(Set<Method> injectedMethods) {
        return injectedMethods
            .stream()
            .map(Method::getParameterTypes)
            .map(paramTypes -> {
                if (paramTypes.length != 1) {
                    throw new IllegalStateException("DI할 메소드 인자는 하나여야 합니다.");
                }

                return paramTypes[0];
            })
            .collect(toSet());
    }

    private static Set<? extends Class<?>> getFieldProperties(Set<Field> injectedFields) {
        return injectedFields
            .stream()
            .map(Field::getType)
            .collect(toSet());
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Constructor<?> getConstructor() {
        return constructor;
    }

    @Override
    public Set<Field> getFields() {
        return fields;
    }

    @Override
    public Method getMethod() {
        return null;
    }

    @Override
    public boolean containsAnnotation(Class<? extends Annotation> annotationType) {
        return type.isAnnotationPresent(annotationType);
    }

    @Override
    public InjectType getInjectType() {
        if (Objects.nonNull(getMethod())) {
            return InjectType.METHOD;
        }

        if (Objects.nonNull(constructor)) {
            return InjectType.CONSTRUCTOR;
        }

        if (!CollectionUtils.isEmpty(fields)) {
            return InjectType.FIELDS;
        }

        return InjectType.NONE;
    }
}
