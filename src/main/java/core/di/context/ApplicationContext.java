package core.di.context;

import core.di.factory.BeanFactory;
import core.di.scanner.ClasspathBeanScanner;
import core.di.scanner.ConfigurationBeanScanner;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ApplicationContext {

    private final BeanFactory beanFactory;
    private final ConfigurationBeanScanner configurationBeanScanner;
    private final ClasspathBeanScanner classpathBeanScanner;

    public ApplicationContext(Class<?> configurationClass) {
        beanFactory = new BeanFactory();
        configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        classpathBeanScanner = new ClasspathBeanScanner(beanFactory);

        initialize(configurationClass);
    }

    private void initialize(Class<?> configurationClass) {
        configurationBeanScanner.register(configurationClass);
        String[] basePackages = configurationBeanScanner.getBasePackages();
        classpathBeanScanner.doScan(basePackages);
        beanFactory.initialize();
    }

    public Set<Class<?>> getBeanClassesWithAnnotation(Class<? extends Annotation> annotation) {
        return classpathBeanScanner.getBeanClassesWithAnnotation(annotation);
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }
}
