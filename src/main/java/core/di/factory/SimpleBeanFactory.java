package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Component;
import core.annotation.Inject;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import static core.di.factory.ReflectionSupport.setFieldByForce;
import static java.util.stream.Collectors.toMap;

/**
 * Scanning and instantiate bean
 * contain bean map with Class key & Instance value
 */
@SuppressWarnings("unchecked")
public class SimpleBeanFactory implements BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(SimpleBeanFactory.class);

    private BeanDefinitions beanDefinitions;
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public SimpleBeanFactory(Object... basePackage) {
        this.beanDefinitions = new BeanScanner(basePackage)
                .scan(Controller.class, Service.class, Repository.class, Component.class);
    }

    /**
     * create beans using bean definition
     */
    public void initialize() {
        beanDefinitions.getBeanDefinitions()
                .forEach(bd -> getBean(bd.getType()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        logger.info("find {}", requiredType);
        if (requiredType == null) {
            return null;
        }

        if (beans.containsKey(requiredType)) {
            return (T) beans.get(requiredType);
        }

        return createBean(requiredType);
    }

    @Override
    public Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotation) {
        return beans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(annotation))
                .collect(toMap(Entry::getKey, Entry::getValue, (b1, b2) -> b2));
    }

    public void registerBeanDefinition(BeanDefinition bd) {
        beanDefinitions.register(bd);
    }

    @SuppressWarnings("unchecked")
    private <T> T createBean(Class<T> clazz) {
        return beanDefinitions.findBeanDefinition(clazz)
                .map(bd -> bd.newInstance(clazz, this::getBean))
                .map(instance -> {
                    populateBean(instance, clazz);
                    beans.put(clazz, instance);
                    return instance;
                })
                .orElse(null);
    }

    private void populateBean(final Object instance, Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isInjectField)
                .forEach(field -> setFieldByForce(field, instance, getBean(field.getType())));
    }

    private boolean isInjectField(Field field) {
        return field != null
                && field.isAnnotationPresent(Inject.class)
                && !BeanUtils.isSimpleValueType(field.getClass());
    }

    public void registerBeanDefinitions(List<BeanDefinition> beanDefinitions) {
        for (BeanDefinition bd : beanDefinitions) {
            registerBeanDefinition(bd);
        }
    }
}
