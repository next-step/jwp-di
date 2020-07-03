package core.di.factory;

import java.util.Set;

/**
 * @author KingCjy
 */
public interface BeanDefinitionRegistry {
    void registerDefinition(BeanDefinition beanDefinition);
    BeanDefinition getBeanDefinition(String name);
    Set<BeanDefinition> getBeanDefinitions(Class<?> type);
}
