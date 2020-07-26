package core.di.config;

import core.di.factory.BeanFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.util.Map;

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
}
