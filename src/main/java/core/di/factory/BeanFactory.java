package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.beans.definition.AnnotatedBeanDefinition;
import core.di.beans.definition.BeanDefinition;
import core.di.beans.definition.BeanDefinitionRegistry;
import core.di.beans.definition.InjectType;
import core.di.beans.getter.BeanGettable;
import core.di.beans.injector.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class BeanFactory implements BeanDefinitionRegistry, BeanGettable {
    public static final Class[] TARGET_ANNOTATION_TYPES = new Class[]{Controller.class, Service.class, Repository.class, Component.class};


    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    private static final Map<InjectType, BeanInjector> beanInjectors = initBeanInjectors();

    private static Map<InjectType, BeanInjector> initBeanInjectors() {
        Map<InjectType, BeanInjector> beanInjectors = Maps.newHashMap();
        beanInjectors.put(InjectType.METHOD, new MethodBeanInjector());
        beanInjectors.put(InjectType.CONSTRUCTOR, new ConstructorBeanInjector());
        beanInjectors.put(InjectType.FIELDS, new FieldBeanInjector());
        beanInjectors.put(InjectType.NONE, new DefaultBeanInjector());
        return beanInjectors;
    }

    public void instantiateBeans() {
        for (Class<?> type : beanDefinitions.keySet()) {
            getBean(type);
        }
    }

    @Override
    public void register(Class<?> type, BeanDefinition beanDefinition) {
        if (!beanDefinitions.containsKey(type)) {
            log.debug("registering BeanDefinition : {}", beanDefinition.getType().getSimpleName());
            beanDefinitions.put(type, beanDefinition);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<?> type) {
        if (beans.containsKey(type)) {
            Object bean = beans.get(type);
            log.debug("getting an existing bean: {}", type.getSimpleName());
            return (T) bean;
        }

        Object bean = getBeanFromDefinition(type);

        if (Objects.nonNull(bean)) {
            log.debug("getting a new bean: {}", type.getSimpleName());
            beans.put(type, bean);
        }

        return (T)bean;
    }

    private <T> T getBeanFromDefinition(Class<T> type) {
        BeanDefinition beanDefinition = beanDefinitions.get(type);

        if (Objects.nonNull(beanDefinition) && InjectType.METHOD.equals(beanDefinition.getInjectType())) {
            return getBeanFromAnnotatedBeanDefinition((AnnotatedBeanDefinition) beanDefinition);
        }

        return getBeanFromDefaultBeanDefinition(type, beanDefinition);
    }

    private <T> T getBeanFromAnnotatedBeanDefinition(AnnotatedBeanDefinition beanDefinition) {
        BeanInjector beanInjector = beanInjectors.get(beanDefinition.getInjectType());
        return beanInjector.inject(this, beanDefinition);
    }

    private <T> T getBeanFromDefaultBeanDefinition(Class<?> type, BeanDefinition beanDefinition) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(type, beanDefinitions.keySet());

        if (Objects.isNull(concreteClass)) {
            return null;
        }

        log.debug("concreteClass: {}", concreteClass);
        beanDefinition = beanDefinitions.get(concreteClass);

        log.debug("beanDefinition: {}", beanDefinition.getType());

        BeanInjector beanInjector = beanInjectors.get(beanDefinition.getInjectType());
        log.debug("beanInjector: {}", beanInjector.getClass().getSimpleName());
        return beanInjector.inject(this, beanDefinition);
    }

    public Map<Class<?>, Object> getControllers() {
        return beanDefinitions.entrySet()
            .stream()
            .filter(entry -> Objects.nonNull(entry.getValue()) && isController(entry.getValue()))
            .collect(toMap(Map.Entry::getKey, entry -> getBean(entry.getKey())));
    }

    private boolean isController(BeanDefinition beanDefinition) {
        return beanDefinition.containsAnnotation(Controller.class);
    }
}