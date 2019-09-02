package core.di.factory;

import core.di.ConfigurationBeanScanner;
import core.di.factory.example.ExampleConfig;
import core.jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author : yusik
 * @date : 29/08/2019
 */
public class ConfigurationBeanScannerTest {

    @DisplayName("팩토리 빈 기본 scan 테스트")
    @Test
    public void defaultScan() {

        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner scanner = new ConfigurationBeanScanner(beanFactory);
        scanner.registerConfiguration(ExampleConfig.class);
        scanner.scan();
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
        assertNotNull(beanFactory.getBean(JdbcTemplate.class));
    }
}
