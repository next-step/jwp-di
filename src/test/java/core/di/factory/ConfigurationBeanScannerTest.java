package core.di.factory;

import core.di.ClasspathBeanScanner;
import core.di.ConfigurationBeanScanner;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
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

    @DisplayName("bean scanner 통합 테스트")
    @Test
    public void register_classpathBeanScanner_통합() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.registerConfiguration(IntegrationConfig.class);
        cbs.scan();
        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanFactory, cbs.getBasePackages());
        cbds.scan();
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);
        assertNotNull(userRepository.getDataSource());

        MyJdbcTemplate jdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate);
        assertNotNull(jdbcTemplate.getDataSource());
    }
}
