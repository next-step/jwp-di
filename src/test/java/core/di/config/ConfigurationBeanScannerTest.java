package core.di.config;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.di.factory.example.ExampleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationBeanScannerTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanScannerTest.class);

    @Test
    void getBeanByFactory() {
        BeanFactory beanFactory = new BeanFactory();
        final Map<Class<?>, Object> scan = ConfigurationBeanScanner.scan(beanFactory);
        final Object bean = scan.get(DataSource.class);
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
}
