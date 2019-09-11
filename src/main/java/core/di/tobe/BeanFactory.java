package core.di.tobe;

import core.di.tobe.bean.BeanDefinition;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

public interface BeanFactory {

    void initialize();

    void registerBeans(Set<BeanDefinition> beanDefinitions);

    <T> T getBean(Class<T> requiredType);

    Map<Class<?>, Object> getBeans(Class<? extends Annotation> repositoryClass);
}
