package core.di.factory;

import core.di.factory.example.ExampleConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigurationBeanScannerTest {

    @Test
    @DisplayName("Configuration 이용한 빈 스캐너 테스트")
    void register_simple() {
        SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.scan(ExampleConfig.class);
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }

}
