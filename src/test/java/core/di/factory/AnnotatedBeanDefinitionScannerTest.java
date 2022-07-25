package core.di.factory;


import core.di.factory.example.ExampleConfig;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnnotatedBeanDefinitionScannerTest {

    @Test
    public void register_simple() {
        BeanFactory beanFactory = new BeanFactory();
        AnnotatedBeanDefinitionScanner scanner = new AnnotatedBeanDefinitionScanner(beanFactory);

        Set<Class<?>> configurationClasses = Sets.newHashSet();
        configurationClasses.add(ExampleConfig.class);
        scanner.registerConfigurationClasses(configurationClasses);

        scanner.scan();
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }
}
