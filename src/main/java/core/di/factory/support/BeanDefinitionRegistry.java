package core.di.factory.support;


import core.di.factory.config.BeanDefinition;

public interface BeanDefinitionRegistry {
    void registerBeanDefinition(BeanDefinition beanDefinition);
}
