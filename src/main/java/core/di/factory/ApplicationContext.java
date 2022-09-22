package core.di.factory;

import core.annotation.ComponentScan;
import core.di.AnnotatedBeanDefinitionReader;
import core.di.BeanDefinitionRegistry;
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

        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanDefinitionRegistry);
        reader.register(this.configurationClasses);

        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
        scanner.scan(getBasePackages(this.configurationClasses));

        this.beanFactory = new BeanFactory(beanDefinitionRegistry);
        this.beanFactory.initialize();
    }

    private Object[] getBasePackages(Set<Class<?>> configurationClasses) {
        return configurationClasses.stream()
                .filter(configurationClass -> configurationClass.isAnnotationPresent(ComponentScan.class))
                .map(componentScan -> componentScan.getAnnotation(ComponentScan.class))
                .map(ComponentScan::value)
                .toArray();
    }

    public Map<Class<?>, Object> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return beanFactory.getBeansAnnotatedWith(annotation);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return this.beanFactory.getBean(requiredType);
    }
}
