package core.di.factory;

import core.di.bean.BeanDefinition;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

public interface BeanFactory {

    void registerBeans(Set<BeanDefinition> beanDefinitions);

    void initialize();

    <T> T getBean(Class<T> requiredType);

    Map<Class<?>, Object> getBeans(Class<? extends Annotation> repositoryClass);
}
