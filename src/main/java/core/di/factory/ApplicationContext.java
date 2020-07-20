package core.di.factory;

import core.annotation.ComponentScan;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by iltaek on 2020/07/20 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class ApplicationContext {

    private final BeanFactory beanFactory;

    public ApplicationContext(Class<?>... configClasses) {
        Object[] basePackage = findBasePackages(configClasses);
        this.beanFactory = new BeanFactory();
        ConfigurationBeanDefinitionScanner configBds = new ConfigurationBeanDefinitionScanner(beanFactory);
        configBds.register(configClasses);

        ClasspathBeanDefinitionScanner classpathBds = new ClasspathBeanDefinitionScanner(beanFactory);
        classpathBds.setAnnotations(Controller.class, Service.class, Repository.class);
        classpathBds.doScan(basePackage);

        this.beanFactory.initialize();
    }

    private Object[] findBasePackages(Class<?>[] configClasses) {
        return Arrays.stream(configClasses)
                     .filter(configClass -> configClass.isAnnotationPresent(ComponentScan.class))
                     .map(configClass -> configClass.getAnnotation(ComponentScan.class))
                     .flatMap(componentScan -> Arrays.stream(componentScan.value()))
                     .toArray();
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return beanFactory.getBeansAnnotatedWith(annotation);
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }
}
