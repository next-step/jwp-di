package core.di.beans.definition;

public interface BeanDefinitionRegistry {
    void register(Class<?> type, BeanDefinition beanDefinition);
}
