package core.di.config;

import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationBeanScannerTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanScannerTest.class);

    @Test
    void getBeanByFactory() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.scan("");
        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
        classpathBeanScanner.scan("");

        final Object bean = classpathBeanScanner.getBean(DataSource.class);
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

    @Test
    void registerSimple() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(ExampleConfig.class);
        beanFactory.initialize();

        assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
    }

    @Test
    void registerClassPathBeanScannerByIntegration() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(IntegrationConfig.class);
        beanFactory.initialize();

        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
        classpathBeanScanner.scan("core.di.factory.example");

        assertThat(beanFactory.getBean(DataSource.class)).isNotNull();

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertThat(userRepository).isNotNull();
        assertThat(userRepository.getDataSource()).isNotNull();

        MyJdbcTemplate jdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertThat(jdbcTemplate).isNotNull();
        assertThat(jdbcTemplate.getDataSource()).isNotNull();
    }
}
