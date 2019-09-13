package core.di.factory;

import next.configuration.AppConfiguration;
import core.di.factory.example.JdbcUserRepository;
import core.jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationBeanScannerTest {

    private Class<AppConfiguration> configurationClass = AppConfiguration.class;

    private PreInstanceBeanHandler pibh;

    @BeforeEach
    void setUp() {
        pibh = new PreInstanceBeanHandler();
    }

    @Test
    void registerBean() {
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(pibh);
        cbs.register(configurationClass);

        assertThat(pibh.isConfigurationBean(DataSource.class)).isTrue();
    }

    @Test
    void registerIntegrationBeanScanners() {
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.registerConfigurationClass(configurationClass);

        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(pibh);
        cbs.register(configurationClass);

        ClassPathBeanScanner cpbs = new ClassPathBeanScanner(pibh);
        cpbs.doScan("core.di.factory.example");

        beanFactory.registerPreInstanceBeanHandler(pibh);
        beanFactory.initializeBeans();

        assertNotNull(beanFactory.getBean(DataSource.class));

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);

        JdbcTemplate jdbcTemplate = beanFactory.getBean(JdbcTemplate.class);
        assertNotNull(jdbcTemplate);
    }
}