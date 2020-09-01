package core.di.config;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {

    public static Map<Class<?>, Object> scan(BeanFactory beanFactory) {
        beanFactory.apply(getClassesByConfigurationType());
        beanFactory.initializeByConfig();
        return beanFactory.getConfigurationBeans();
    }

    private static Set<Class<?>> getClassesByConfigurationType() {
        final Set<String> paths = getPathToScan();
        return paths.stream()
                .flatMap(path -> {
                    Reflections componentReflections = new Reflections(path);
                    return componentReflections.getTypesAnnotatedWith(Configuration.class).stream();
                })
                .collect(Collectors.toSet());
    }

    private static Set<String> getPathToScan() {
        final Set<ComponentScan> componentScans = getClassesByComponentScanType();
        return componentScans.stream()
                .flatMap(s -> Arrays.stream(s.value()))
                .collect(Collectors.toSet());
    }

    private static Set<ComponentScan> getClassesByComponentScanType() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
        return typesAnnotatedWith.stream()
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class))
                .collect(Collectors.toSet());
    }
}
