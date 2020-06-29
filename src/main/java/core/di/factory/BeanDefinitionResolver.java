package core.di.factory;

public interface BeanDefinitionResolver {
    BeanDefinition resolve(Class<?> beanClass);
}
