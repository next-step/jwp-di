package core.di.factory;

import java.util.List;

public interface BeanFactory {

    void initialize();

    void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition);

    <T> T getBean(Class<T> requiredType);

    List<Class<?>> getBeanClasses();

    List<Object> getBeans();
}
