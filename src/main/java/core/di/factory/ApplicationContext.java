package core.di.factory;

import core.di.AnnotatedBeanDefinitionScanner;
import core.di.BeanDefinitionRegistry;
import core.di.BeanScanner;
import core.di.ClassPathBeanDefinitionScanner;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContext {
    private BeanFactory beanFactory;
    private final Set<Class<?>> configurationClasses;

    public ApplicationContext(Class<?>... configurationClasses) {
        this.configurationClasses = Arrays.stream(configurationClasses)
                .collect(Collectors.toSet());
    }

    public void initialize() {
        BeanDefinitionRegistry beanDefinitionRegistry = new BeanDefinitionRegistry();

        BeanScanner annotatedBeanDefinitionScanner = new AnnotatedBeanDefinitionScanner(beanDefinitionRegistry);
        annotatedBeanDefinitionScanner.scan(this.configurationClasses);

        BeanScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
        classPathBeanDefinitionScanner.scan(this.configurationClasses);

        this.beanFactory = new BeanFactory(beanDefinitionRegistry);
        this.beanFactory.initialize();
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return beanFactory.getBeansAnnotatedWith(annotation);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return this.beanFactory.getBean(requiredType);
    }
}
