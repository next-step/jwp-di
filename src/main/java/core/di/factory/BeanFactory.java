package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static core.di.factory.ClasspathBeanScanner.CLASSPATH_TARGET_TYPES;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class BeanFactory {
    public static final Set<Class<? extends Annotation>> TARGET_ANNOTATION_TYPES = Sets.newHashSet(Arrays.asList(Controller.class, Service.class, Repository.class, Component.class));
    public static final Class CONTROLLER_CLASS = Controller.class;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private Map<Class<?>, BeanDefinitionResolver> resolvers = Maps.newHashMap();
    private Set<String> basePackages = Sets.newHashSet();
    private Set<Class<?>> configurationTypes = Sets.newHashSet();

    public void initialize() {
        if (!CollectionUtils.isEmpty(configurationTypes)) {
            registerBeanDefinitions(configurationTypes);
        }
    }

    public void registerConfigurationTypes(Class<?> configurationType) {
        if (Objects.nonNull(configurationType)) {
            configurationTypes.add(configurationType);
        }
    }

    public Set<Class<?>> getClassPathRootTypes() {
        Set<Class<?>> rootTypes = Sets.newHashSet();

        TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
        SubTypesScanner subTypesScanner = new SubTypesScanner();
        MethodAnnotationsScanner methodAnnotationsScanner = new MethodAnnotationsScanner();

        for (String basePackage : basePackages) {
            Reflections reflections = new Reflections(basePackage, typeAnnotationsScanner, subTypesScanner, methodAnnotationsScanner);
            rootTypes.addAll(ReflectionUtils.getTypesAnnotatedWith(reflections, CLASSPATH_TARGET_TYPES));
        }

        return rootTypes;
    }

    public void registerBeanDefinitions(Set<Class<?>> rootTypes) {
        if (CollectionUtils.isEmpty(rootTypes)) {
            return;
        }

        registerResolvers(rootTypes);
        registerBeanDefinitions();
    }

    public void registerResolvers(Set<Class<?>> rootTypes) {
        for (Class<?> type : rootTypes) {
            if (!resolvers.containsKey(type)) {
                putResolvers(rootTypes, type);
            }
        }

        resolvers.values().forEach(
            resolver -> log.debug("resolvers: {}", resolver.getType())
        );
    }

    public void registerBasePackages(String[] basePackages) {
        if (!ArrayUtils.isEmpty(basePackages)) {
            this.basePackages.addAll(Arrays.asList(basePackages));
        }
    }

    private void putResolvers(Set<Class<?>> rootTypes, Class<?> type) {
        if (isAnnotatedType(type)) {
            resolvers.put(type, new ConstructorBeanDefinitionResolver(rootTypes, type, beanDefinitions, resolvers));
        }
        else {
            Set<Method> methods = BeanFactoryUtils.getAnnotatedBeanMethods(type);
            Object parent = BeanUtils.instantiateClass(type);

            for (Method method : methods) {
                if (resolvers.containsKey(method.getReturnType())) {
                    continue;
                }

                log.debug("putResolvers - methodName: {}, returnType: {}", method.getName(), method.getReturnType().getSimpleName());
                resolvers.put(method.getReturnType(), new MethodBeanDefinitionResolver(rootTypes, method.getReturnType(), beanDefinitions, resolvers, method, parent));
            }
        }
    }

    private void registerBeanDefinitions() {
        if (CollectionUtils.isEmpty(resolvers)) {
            return;
        }

        for (Map.Entry<Class<?>, BeanDefinitionResolver> resolverEntry : resolvers.entrySet()) {
            if (beanDefinitions.containsKey(resolverEntry.getKey())) {
                continue;
            }

            BeanDefinition beanDefinition = resolverEntry.getValue().resolve();

            if (Objects.nonNull(beanDefinition)) {
                beanDefinitions.put(resolverEntry.getKey(), beanDefinition);
                log.debug("beanDefinitions: {}, {}", beanDefinition.getType().getSimpleName());
            }
        }
    }

    private boolean isAnnotatedType(Class<?> type) {
        return TARGET_ANNOTATION_TYPES
            .stream()
            .anyMatch(type::isAnnotationPresent);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        if (beans.containsKey(requiredType)) {
            return (T) beans.get(requiredType);
        }

        Object bean = getBeanFromDefinition(requiredType);

        if (Objects.nonNull(bean)) {
            beans.put(requiredType, bean);
        }

        return (T)bean;
    }

    private <T> Object getBeanFromDefinition(Class<T> type) {
        BeanDefinition beanDefinition = beanDefinitions.get(type);

        if (Objects.isNull(beanDefinition)) {
            return null;
        }

        try {
            if (Objects.isNull(beanDefinition.getMethod())) {
                return getBeanByConstructor(beanDefinition);
            }
            else {
                return getBeanByMethodInvocation(beanDefinition);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private Object getBeanByConstructor(BeanDefinition beanDefinition) {
        if (Objects.isNull(beanDefinition.getConstructor())) {
            return BeanUtils.instantiateClass(beanDefinition.getType());
        }

        if (CollectionUtils.isEmpty(beanDefinition.getChildren())) {
            return BeanUtils.instantiateClass(beanDefinition.getConstructor());
        }

        return getParameterizedBean(beanDefinition);
    }

    private Object getBeanByMethodInvocation(BeanDefinition beanDefinition) throws IllegalAccessException, InvocationTargetException {
        if (CollectionUtils.isEmpty(beanDefinition.getChildren())) {
            return beanDefinition.getMethod().invoke(beanDefinition.getParent());
        }

        return getParameterizedBean(beanDefinition);
    }

    private Object getParameterizedBean(BeanDefinition beanDefinition) {
        List<Object> parameters = Lists.newArrayList();

        for (BeanDefinition child : beanDefinition.getChildren()) {
            if (beans.containsKey(child.getType())) {
                parameters.add(beans.get(child.getType()));
            }
            else {
                Object beanInstance = getBean(child.getType());
                beans.put(child.getType(), beanInstance);
                parameters.add(beanInstance);
            }
        }

        return instantiateParameterizedClass(beanDefinition, parameters);
    }

    private Object instantiateParameterizedClass(BeanDefinition beanDefinition, List<Object> parameters) {
        try {
            if (Objects.isNull(beanDefinition.getConstructor())) {
                return beanDefinition.getMethod().invoke(beanDefinition.getParent(), parameters.toArray(new Object[0]));
            }

            return BeanUtils.instantiateClass(beanDefinition.getConstructor(), parameters.toArray(new Object[0]));
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public Map<Class<?>, Object> getControllers() {
        return beanDefinitions.entrySet()
            .stream()
            .filter(entry -> Objects.nonNull(entry.getValue()) && entry.getValue().getAnnotations().contains(CONTROLLER_CLASS))
            .collect(toMap(Map.Entry::getKey, entry -> getBean(entry.getKey())));
    }

    public void clearResolvers() {
        resolvers.clear();
    }
}