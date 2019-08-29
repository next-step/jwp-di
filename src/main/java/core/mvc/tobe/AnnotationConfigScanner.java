package core.mvc.tobe;

import core.annotation.ComponentScan;
import core.di.factory.BeanFactory;

import java.util.Arrays;
import java.util.stream.Stream;

public class AnnotationConfigScanner {

    private BeanFactory beanFactory;

    public AnnotationConfigScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void loadConfig(Class<?>... annotatedClasses) {
        AnnotatedBeanDefinition annotatedBeanDefinition = new AnnotatedBeanDefinition(beanFactory);
        annotatedBeanDefinition.registerBean(annotatedClasses);
        Object[] basePackages = findBasePackages(annotatedClasses);
        BeanScanner beanScanner = new BeanScanner(beanFactory);
        beanScanner.doScan(basePackages);
    }

    private Object[] findBasePackages(Class<?>[] annotatedClasses) {
        return Arrays.stream(annotatedClasses)
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class).value())
                .flatMap(Stream::of)
                .toArray();
    }
}
