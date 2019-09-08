package core.di.factory;

import next.configuration.AppConfiguration;
import core.di.factory.example.JdbcUserRepository;
import core.jdbc.JdbcTemplate;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationBeanScannerTest {
    @Test
    void registerBean() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(AppConfiguration.class);
        beanFactory.initializeConfigBeans();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }

    @Test
    void registerIntegrationBeanScanners() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(AppConfiguration.class);
        beanFactory.initializeConfigBeans();

        ClassPathBeanScanner cpbs = new ClassPathBeanScanner(beanFactory);
        cpbs.doScan("core.di.factory.example");
        beanFactory.initializeClassPathBeans();

        assertNotNull(beanFactory.getBean(DataSource.class));

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);

        JdbcTemplate jdbcTemplate = beanFactory.getBean(JdbcTemplate.class);
        assertNotNull(jdbcTemplate);
    }
}
