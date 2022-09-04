package core.di;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition);

    BeanDefinition getBeanDefinition(Class<?> clazz);
}
