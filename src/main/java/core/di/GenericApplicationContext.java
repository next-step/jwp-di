package core.di;

import java.util.Map;
import java.util.Set;

import core.di.factory.DefaultListableBeanFactory;

public class GenericApplicationContext implements BeanDefinitionRegistry, ApplicationContext {

    private final DefaultListableBeanFactory beanFactory;

    public GenericApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    public void refresh() {
        beanFactory.preInstantiateSingletons();
    }

    @Override
    public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
        beanFactory.registerBeanDefinition(clazz, beanDefinition);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> requiredType) {
        return beanFactory.getBeanDefinition(requiredType);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        return beanFactory.getBeansOfType(type);
    }

    @Override
    public Set<Class<?>> getBeanClasses() {
        return beanFactory.getBeanClasses();
    }
}
