package core.di.config;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {

    public static Map<Class<?>, Object> scan(BeanFactory beanFactory) {
        Reflections reflections = new Reflections("");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
        Set<Class<?>> classes = new HashSet<>();
        typesAnnotatedWith.stream()
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .forEach(clazz -> {
                    final ComponentScan annotation = clazz.getAnnotation(ComponentScan.class);
                    final String[] value = annotation.value();
                    for (final String path : value) {
                        Reflections componentReflections = new Reflections(path);
                        classes.addAll(componentReflections.getTypesAnnotatedWith(Configuration.class));
                    }
                });

        final Set<ComponentScan> componentScans = typesAnnotatedWith.stream()
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class))
                .collect(Collectors.toSet());

        final Set<String> paths = componentScans.stream()
                .flatMap(s -> Arrays.stream(s.value()))
                .collect(Collectors.toSet());

        final Set<Class<?>> configClasses = paths.stream()
                .flatMap(path -> {
                    Reflections componentReflections = new Reflections(path);
                    return componentReflections.getTypesAnnotatedWith(Configuration.class).stream();
                })
                .collect(Collectors.toSet());

        beanFactory.applyConfiguration(configClasses);
        return beanFactory.getConfigurationBeans();
    }
}
