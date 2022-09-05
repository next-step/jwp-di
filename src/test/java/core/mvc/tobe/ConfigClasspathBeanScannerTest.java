package core.mvc.tobe;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.di.factory.BeanFactory;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import javax.sql.DataSource;
import next.config.DataConfiguration;
import org.junit.jupiter.api.Test;

class ConfigClasspathBeanScannerTest {

    @Test
    void register_classpathBeanScanner_통합() {
        BeanFactory beanFactory = new BeanFactory();

        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(DataConfiguration.class);

        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanFactory);
        cbds.doScan("core.di");

        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);
        assertNotNull(userRepository.getDataSource());

        MyJdbcTemplate jdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate);
        assertNotNull(jdbcTemplate.getDataSource());
    }

    @Test
    void register_simple() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner configBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configBeanScanner.register(DataConfiguration.class);
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }

}