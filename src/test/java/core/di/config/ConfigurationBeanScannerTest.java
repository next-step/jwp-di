package core.di.config;

import core.annotation.Component;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.di.factory.example.ExampleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationBeanScannerTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanScannerTest.class);

    @Test
    void getBeanByFactory() {
        BeanScanner beanScanner = new BeanScanner();
        beanScanner.scan("");

        final Object bean = BeanScanner.getBean(DataSource.class);
        assertThat(bean).isNotNull();
    }

    @Test
    void getComponentScanClazz() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
        Set<Class<?>> classes = new HashSet<>();
        typesAnnotatedWith.stream()
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .forEach(clazz -> {
                    final ComponentScan annotation = clazz.getAnnotation(ComponentScan.class);
                    final String[] value = annotation.value();
                    for (final String path : value) {
                        Reflections reflections1 = new Reflections(path);
                        classes.addAll(reflections1.getTypesAnnotatedWith(Configuration.class));
                    }
                });

        assertThat(classes.contains(MyConfiguration.class)).isTrue();
        assertThat(classes.contains(ExampleConfig.class)).isTrue();
    }

    @Test
    void getComponentScansPath() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
        final Set<ComponentScan> componentScans = typesAnnotatedWith.stream()
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class))
                .collect(Collectors.toSet());

        final Set<String> paths = componentScans.stream()
                .flatMap(s -> Arrays.stream(s.value()))
                .collect(Collectors.toSet());

        assertThat(paths.contains("next")).isTrue();
        assertThat(paths.contains("core")).isTrue();

    }

    @Test
    void getScanningConfigurationClasses() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Configuration.class);
        final Set<ComponentScan> componentScans = typesAnnotatedWith.stream()
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class))
                .collect(Collectors.toSet());

        final Set<String> paths = componentScans.stream()
                .flatMap(s -> Arrays.stream(s.value()))
                .collect(Collectors.toSet());

        final Set<Class<?>> classes = paths.stream()
                .flatMap(path -> {
                    Reflections componentReflections = new Reflections(path);
                    return componentReflections.getTypesAnnotatedWith(Configuration.class).stream();
                })
                .collect(Collectors.toSet());

        assertThat(classes.contains(MyConfiguration.class)).isTrue();
        assertThat(classes.contains(ExampleConfig.class)).isTrue();
    }
}
