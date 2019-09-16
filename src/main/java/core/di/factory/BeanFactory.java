package core.di.factory;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final BeanRegistry beanRegistry;
    private final BeanTypeRegistry beanTypeRegistry;
    private final List<BeanInitializer> initializers;

    public BeanFactory() {
        beanRegistry = new BeanRegistry();
        beanTypeRegistry = new BeanTypeRegistry();
        initializers = ImmutableList.of(
                new BeanMethodTypeInitializer(this), new ComponentTypeInitializer(this)
        );
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        T bean = getRegistryBean(requiredType);
        if (Objects.nonNull(bean)) {
            return bean;
        }

        initializeBean(requiredType);

        return getRegistryBean(requiredType);
    }

    private <T> T getRegistryBean(Class<T> requiredType) {
        if (beanRegistry.contains(requiredType)) {
            return beanRegistry.getBean(requiredType);
        }

        Object type = beanTypeRegistry.getType(requiredType);
        if (type instanceof Class) {
            return beanRegistry.getBean((Class<T>) type);
        }

        return null;
    }

    public void initialize() {
        for (Class<?> beanClass : beanTypeRegistry.getAllBeanClasses()) {
            initializeBean(beanClass);
        }
    }

    private void initializeBean(Class<?> clazz) {
        Object type = beanTypeRegistry.getType(clazz);

        this.initializers.stream()
            .filter(initializer -> initializer.support(type))
            .findAny()
            .map(initializer -> initializer.initialize(beanRegistry, type));
    }

    public void addAllBeanClasses(Collection<Class<?>> beanClasses) {
        beanClasses.forEach(beanTypeRegistry::addType);
    }

    public boolean containsType(Class<?> type) {
        return beanTypeRegistry.contains(type);
    }

    public Set<Class<?>> getAllBeanClasses() {
        return beanTypeRegistry.getAllBeanClasses();
    }
}
