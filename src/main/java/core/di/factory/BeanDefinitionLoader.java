package core.di.factory;

public interface BeanDefinitionLoader<T> {
    void loadBeanDefinitions(T... loadParams);
}
