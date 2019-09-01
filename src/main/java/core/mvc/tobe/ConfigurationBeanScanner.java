package core.mvc.tobe;

import core.annotation.ComponentScan;
import core.di.factory.BeanFactory;

import java.util.Arrays;
import java.util.stream.Stream;

public class ConfigurationBeanScanner {

    private BeanFactory beanFactory;
    private Object[] basePackages;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?>... annotatedClasses) {
        AnnotatedBeanDefinition annotatedBeanDefinition = new AnnotatedBeanDefinition(beanFactory);
        annotatedBeanDefinition.registerBean(annotatedClasses);
        this.basePackages = findBasePackages(annotatedClasses);
    }

    private Object[] findBasePackages(Class<?>[] annotatedClasses) {
        return Arrays.stream(annotatedClasses)
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class).value())
                .flatMap(Stream::of)
                .toArray();
    }

    public Object[] getBasePackages() {
        return this.basePackages;
    }
}
