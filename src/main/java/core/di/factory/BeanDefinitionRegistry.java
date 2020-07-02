package core.di.factory;

/**
 * @author KingCjy
 */
public interface BeanDefinitionRegistry {
    void registerDefinition(BeanDefinition beanDefinition);
    BeanDefinition getBeanDefinition(Class<?> type);
    BeanDefinition getBeanDefinition(String name);
}
