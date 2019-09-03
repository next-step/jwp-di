package core.di.factory;

import core.di.bean.BeanDefinition;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by hspark on 2019-09-04.
 */
public interface BeanFactory {

    <T> T getBean(Class<T> requiredType);

    Map<Class<?>, Object> getByAnnotation(Class<? extends Annotation> annotation);

    void initialize();

    void registerBeanDefinition(BeanDefinition beanDefinition);
}
