package core.di.factory.example;

import core.di.factory.BeanDefinitions;
import core.di.factory.BeanFactory;
import core.di.factory.ClasspathBeanScanner;
import core.di.factory.ConfigurationBeanScanner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationBeanScannerTest {

    @DisplayName("설정파일을 등록해서 Bean을 스캔한다.")
    @Test
    public void register_simple() {
        BeanDefinitions beanDefinitions = new BeanDefinitions();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanDefinitions);
        cbs.register(ExampleConfig.class);
        BeanFactory beanFactory = new BeanFactory(beanDefinitions);
        beanFactory.initialize();

        assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
    }

    @DisplayName("classpathBeanScanner와 설정파일을 통합한다.")
    @Test
    public void register_classpathBeanScanner_통합() {
        BeanDefinitions beanDefinitions = new BeanDefinitions();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanDefinitions);
        cbs.register(IntegrationConfig.class);

        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanDefinitions);
        cbds.doScan("core.di.factory.example");
        BeanFactory beanFactory = new BeanFactory(beanDefinitions);
        beanFactory.initialize();

        assertThat(beanFactory.getBean(DataSource.class)).isNotNull();

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertThat(userRepository).isNotNull();

        MyJdbcTemplate jdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertThat(jdbcTemplate).isNotNull();
        assertThat(jdbcTemplate.getDataSource()).isNotNull();
    }
}
