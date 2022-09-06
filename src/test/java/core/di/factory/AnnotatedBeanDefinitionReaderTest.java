package core.di.factory;

import static org.assertj.core.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import core.di.AnnotatedBeanDefinitionReader;
import core.di.factory.example.ExampleConfig;

class AnnotatedBeanDefinitionReaderTest {

    @Test
    void register() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AnnotatedBeanDefinitionReader scanner = new AnnotatedBeanDefinitionReader(beanFactory);
        scanner.register(ExampleConfig.class);
        beanFactory.preInstantiateSingletons();

        assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
    }
}
