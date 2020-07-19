package core.di.factory;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.example.ExampleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationTest.class);

    private Reflections reflections;
    private Set<Class<?>> configurationClasses;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(""))
                .addScanners(new MemberUsageScanner(), new MethodAnnotationsScanner(), new MethodParameterScanner()));
        configurationClasses = reflections.getTypesAnnotatedWith(Configuration.class);
        beanFactory = new BeanFactory(configurationClasses);
        beanFactory.initialize();
    }

    @Test
    public void scanConfiguration() {
        configurationClasses.contains(ExampleConfig.class);
    }

    @Test
    public void scanConfigurationBean() throws Exception {
        Class<?> configurationClass = configurationClasses.iterator().next();
        Object instance = configurationClass.newInstance();

        Method[] methods = configurationClass.getDeclaredMethods();
        List<Method> methodList = Arrays.stream(methods)
                .filter(m -> m.getDeclaredAnnotation(Bean.class) != null)
                .collect(Collectors.toList());
        for (Method method : methodList) {
            logger.debug("{}", method);
            Class<?> returnType = method.getReturnType();
            Object obj = method.invoke(instance);

            logger.debug("{}, {}, {}", method, returnType, obj);
        }
    }

    @Test
    public void addConfigurationBean() throws Exception {
        Class<?> configurationClass = ExampleConfig.class;
        Method method = configurationClass.getMethod("dataSource");
        Object dataSource = beanFactory.getBean(method.getReturnType());
        assertThat(dataSource).isNotEqualTo(null);
    }

}
