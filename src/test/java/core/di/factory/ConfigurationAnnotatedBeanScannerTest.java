package core.di.factory;

import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.MyJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationAnnotatedBeanScannerTest {
    private ConfigurationBeanScanner beanScanner;
    private DefaultBeanFactory defaultBeanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        defaultBeanFactory = new DefaultBeanFactory();
        beanScanner = new ConfigurationBeanScanner(defaultBeanFactory);
        beanScanner.registerConfiguration(IntegrationConfig.class);
        beanScanner.scan();

        defaultBeanFactory.initialize();
    }

    @Test
    public void di() {
        DataSource dataSource = defaultBeanFactory.getBean(DataSource.class);

        assertNotNull(dataSource);

        MyJdbcTemplate jdbcTemplate = defaultBeanFactory.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate.getDataSource());
    }
}
