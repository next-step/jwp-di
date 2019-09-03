package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.ComponentScan;
import core.di.bean.BeanDefinition;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by hspark on 2019-09-04.
 */
public class ApplicationContext implements BeanFactory {

    private final Set<Class<?>> configurations;
    private final BeanFactory beanFactory = new DefaultBeanFactory();

    public ApplicationContext(Class<?>... configurationClasses) {
        this.configurations = Sets.newHashSet(configurationClasses);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    @Override
    public Map<Class<?>, Object> getByAnnotation(Class<? extends Annotation> annotation) {
        return beanFactory.getByAnnotation(annotation);
    }

    @Override
    public void initialize() {
        Object[] basePackages = getBasePackages();
        AnnotatedBeanScanner annotatedBeanScanner = new AnnotatedBeanScanner(beanFactory, basePackages);
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.registerConfiguration(configurations);

        annotatedBeanScanner.scan();
        configurationBeanScanner.scan();
        beanFactory.initialize();
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        beanFactory.registerBeanDefinition(beanDefinition);
    }


    private Object[] getBasePackages() {
        return configurations.stream()
                .filter(it -> Objects.nonNull(it.getAnnotation(ComponentScan.class)))
                .flatMap(it -> Arrays.stream(it.getAnnotation(ComponentScan.class).value()))
                .collect(Collectors.toList()).toArray();
    }
}
