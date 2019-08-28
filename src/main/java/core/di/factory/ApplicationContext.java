package core.di.factory;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContext {

    private SimpleBeanFactory simpleBeanFactory;

    public ApplicationContext(Class<?>... classes) {
        simpleBeanFactory = new SimpleBeanFactory();
        register(classes);
        simpleBeanFactory.initialize();
    }

    public ApplicationContext(String... basePackages) {
        simpleBeanFactory = new SimpleBeanFactory();
        register(basePackages);
        simpleBeanFactory.initialize();
    }

    private void register(Class<?>[] classes) {
        for (Class<?> clazz : classes) {
            registerBean(clazz);
        }
    }

    private void register(String[] basePackages) {
        for (String basePackage : basePackages) {
            registerBean(basePackage);
        }
    }

    private void registerBean(String basePackage) {
        simpleBeanFactory.registerBeanDefinitions(BeanScanner.scan(basePackage));
    }

    private void registerBean(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Configuration.class)) {
            return;
        }

        if (clazz.isAnnotationPresent(ComponentScan.class)) {
            final ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            simpleBeanFactory.registerBeanDefinitions(BeanScanner.scan((Object[]) componentScan.basePackages()));
        }

        Object target = BeanUtils.instantiateClass(clazz);
        Set<BeanDefinition> beanDefinitions = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(method -> new BeanDefinition(target, method))
                .collect(Collectors.toSet());
        simpleBeanFactory.registerBeanDefinitions(beanDefinitions);
    }

    public Map<Class<?>, Object> getBeans(Class<? extends Annotation> annotation) {
        return simpleBeanFactory.getBeans(annotation);
    }

    public <T> T getBean(Class<T> requiredType) {
        return simpleBeanFactory.getBean(requiredType);
    }

}
