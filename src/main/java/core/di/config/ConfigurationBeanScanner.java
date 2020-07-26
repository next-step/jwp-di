package core.di.config;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        beanFactory.applyConfiguration(classes);
        return beanFactory.getConfigurationBeans();
    }
}
