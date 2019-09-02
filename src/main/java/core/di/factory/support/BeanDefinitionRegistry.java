package core.di.factory.support;


import core.di.factory.config.BeanDefinition;

import java.util.Set;

public interface BeanDefinitionRegistry {
    void registerBeanDefinition(BeanDefinition beanDefinition);

    Set<BeanDefinition> getDefinitions();
}
