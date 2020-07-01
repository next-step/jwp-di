package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class BeanFactory {
    public static final Set<Class<? extends Annotation>> TARGET_ANNOTATION_TYPES = Sets.newHashSet(Arrays.asList(Controller.class, Service.class, Repository.class, Component.class));
    public static final Class CONTROLLER_CLASS = Controller.class;

    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private Map<Class<?>, BeanDefinitionResolver> resolvers = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> rootTypes) {
        for (Class<?> type : rootTypes) {
            if (!resolvers.containsKey(type)) {
                putResolvers(rootTypes, type);
            }
        }

        resolvers.values().forEach(
            resolver -> log.debug("resolvers: {}", resolver.getType())
        );
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

    private boolean isAnnotatedType(Class<?> type) {
        return TARGET_ANNOTATION_TYPES
            .stream()
            .anyMatch(type::isAnnotationPresent);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.getOrDefault(requiredType, getBeanFromDefinition(requiredType));
    }

    public void initialize() {
        for (Map.Entry<Class<?>, BeanDefinitionResolver> resolverEntry : resolvers.entrySet()) {
            if (beanDefinitions.containsKey(resolverEntry.getKey())) {
                continue;
            }

            beanDefinitions.put(resolverEntry.getKey(), resolverEntry.getValue().resolve());
        }

        beanDefinitions.values().forEach(
            beanDefinition -> log.debug("beanDefinitions: {}, {}", beanDefinition.getType().getSimpleName(), beanDefinition.getAnnotations())
        );
    }

    private <T> Object getBeanFromDefinition(Class<T> type) {
        BeanDefinition beanDefinition = beanDefinitions.get(type);

        if (Objects.isNull(beanDefinition)) {
            return null;
        }

        try {
            if (Objects.isNull(beanDefinition.getMethod())) {
                if (Objects.isNull(beanDefinition.getConstructor())) {
                    return BeanUtils.instantiateClass(beanDefinition.getType());
                }

                if (CollectionUtils.isEmpty(beanDefinition.getChildren())) {
                    return BeanUtils.instantiateClass(beanDefinition.getConstructor());
                }

                return getParameterizedBean(beanDefinition);
            }
            else {
                if (CollectionUtils.isEmpty(beanDefinition.getChildren())) {
                    return beanDefinition.getMethod().invoke(beanDefinition.getParent(), beanDefinition.getChildren().toArray(new Object[0]));
                }

                return getParameterizedBean(beanDefinition);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
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
            .filter(entry -> entry.getValue().getAnnotations().contains(CONTROLLER_CLASS))
            .collect(toMap(Map.Entry::getKey, entry -> getBean(entry.getKey())));
    }
}