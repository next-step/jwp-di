package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.ComponentScan;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ApplicationContext {

    private final BeanFactory beanFactory = new BeanFactory();
    private final Set<Class<?>> configurationClasses;

    public ApplicationContext(Class<?>... configurationClasses) {
        this.configurationClasses = Sets.newHashSet(configurationClasses);
    }

    public void initialize() {
        ClasspathBeanDefinitionScanner classpathBeanDefinitionScanner = new ClasspathBeanDefinitionScanner(beanFactory, findBasePackages());
        classpathBeanDefinitionScanner.scan();

        AnnotatedBeanDefinitionScanner annotatedBeanDefinitionScanner = new AnnotatedBeanDefinitionScanner(beanFactory);
        annotatedBeanDefinitionScanner.registerConfigurationClasses(configurationClasses);
        annotatedBeanDefinitionScanner.scan();

        beanFactory.initialize();
    }

    public Map<Class<?>, Object> getByAnnotation(Class<? extends Annotation> clazz) {
        return beanFactory.getByAnnotation(clazz);
    }

    private Object[] findBasePackages() {
        return configurationClasses.stream()
                .filter(it -> it.isAnnotationPresent(ComponentScan.class))
                .flatMap(it -> Arrays.stream(it.getAnnotation(ComponentScan.class).value()))
                .toArray();
    }
}
