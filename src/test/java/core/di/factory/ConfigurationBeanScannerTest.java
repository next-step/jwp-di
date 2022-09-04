package core.di.factory;

import static org.assertj.core.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import core.di.ConfigurationBeanScanner;
import core.di.factory.BeanFactory;
import core.di.factory.example.ExampleConfig;

class ConfigurationBeanScannerTest {

    @Test
    void register() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner scanner = new ConfigurationBeanScanner(beanFactory);
        scanner.register(ExampleConfig.class);
        beanFactory.initialize();

        assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
    }
}
