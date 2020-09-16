package core.di.config;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {

    private BeanFactory beanFactory;

    public ConfigurationBeanScanner(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> clazz) {
        beanFactory.register(clazz);
    }

    public void scan(Object... basePackage) {
        this.beanFactory.apply(getClassesByConfigurationType(basePackage));
        this.beanFactory.apply(getClasses(basePackage));
        this.beanFactory.initialize();
    }

    private Set<Class<?>> getClasses(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
        return typesAnnotatedWith.stream()
                .filter(clazz -> !clazz.isAnnotationPresent(ComponentScan.class))
                .collect(Collectors.toSet());
    }

    private Set<Class<?>> getClassesByConfigurationType(Object... basePackage) {
        final Set<String> paths = getPathToScan(basePackage);
        return paths.stream()
                .flatMap(path -> {
                    Reflections componentReflections = new Reflections(path);
                    return componentReflections.getTypesAnnotatedWith(Configuration.class).stream();
                })
                .collect(Collectors.toSet());
    }

    private Set<String> getPathToScan(Object... basePackage) {
        final Set<ComponentScan> componentScans = getClassesByComponentScanType(basePackage);
        return componentScans.stream()
                .flatMap(s -> Arrays.stream(s.value()))
                .collect(Collectors.toSet());
    }

    private Set<ComponentScan> getClassesByComponentScanType(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
        return typesAnnotatedWith.stream()
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class))
                .collect(Collectors.toSet());
    }
}
