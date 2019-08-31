package core.di.factory;

import java.util.Set;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(BeanDefinition bd);

    void registerBeanDefinitions(Set<BeanDefinition> beanDefinitions);

}
