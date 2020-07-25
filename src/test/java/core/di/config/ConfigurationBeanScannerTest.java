package core.di.config;

import core.annotation.ComponentScan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationBeanScannerTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanScannerTest.class);

    private ConfigurationBeanScanner configurationBeanScanner;

    @BeforeEach
    void setUp() {
        configurationBeanScanner = new ConfigurationBeanScanner();
    }

    @Test
    void configurationObjectCountTest() {
        int size = configurationBeanScanner.getTypesAnnotatedWith().size();
        /**
         * core.di.config.MyConfiguration
         * core.di.factory.example.IntegrationConfig
         * core.di.factory.example.ExampleConfig
         */
        assertThat(size).isEqualTo(3);
    }

    @Test
    void getComponentScan() {
        Set<Class<?>> typesAnnotatedWith = configurationBeanScanner.getTypesAnnotatedWith();
        typesAnnotatedWith.forEach(clazz -> {
            logger.info(clazz.getName() + " : " + clazz.isAnnotationPresent(ComponentScan.class));
        });
    }

    @Test
    void getBeanByFactory() {
        configurationBeanScanner.scan();
        final Object bean = configurationBeanScanner.getBean(DataSource.class);
        assertThat(bean).isNotNull();
    }
}
