package core.di.context;

import core.annotation.ComponentScan;
import core.di.ClasspathBeanScanner;
import core.di.ConfigurationBeanScanner;
import core.di.factory.BeanFactory;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    private final BeanFactory beanFactory;

    public AnnotationConfigApplicationContext(Class<?> clazz) {
        this.beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(this.beanFactory);
        cbs.register(clazz);

        ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
        if(componentScan != null){
            ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(this.beanFactory);
            classpathBeanScanner.doScan(Stream.of(componentScan.basePackages()).collect(Collectors.toSet()));
        }

        this.beanFactory.initialize();
    }

    @Override
    public Object[] getBeans(Class<? extends Annotation> annotation) {
        return beanFactory.getBeansByAnnotation(annotation);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

}
