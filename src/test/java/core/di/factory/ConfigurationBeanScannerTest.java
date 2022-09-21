package core.di.factory;

import core.di.factory.definition.BeanDefinitionRegistry;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.scanner.ClassPathBeanScanner;
import core.di.factory.scanner.ConfigurationBeanScanner;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
public class ConfigurationBeanScannerTest {


    @DisplayName("설정파일을 등록해서 Bean을 스캔한다.")
    @Test
    public void register_simple() {
        BeanDefinitionRegistry beanDefinitions = new BeanDefinitionRegistry();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanDefinitions);
        cbs.doScan(ExampleConfig.class);
        BeanFactory beanFactory = new BeanFactory(beanDefinitions);
        beanFactory.register();

        Assertions.assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
    }
    @Test
    void resister() {
        BeanDefinitionRegistry beanDefinitions = new BeanDefinitionRegistry();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanDefinitions);
        cbs.doScan(IntegrationConfig.class);

        ClassPathBeanScanner cbds = new ClassPathBeanScanner(beanDefinitions);
        cbds.doScan(IntegrationConfig.class);
        BeanFactory beanFactory = new BeanFactory(beanDefinitions);

        beanFactory.register();

        Assertions.assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        Assertions.assertThat(userRepository).isNotNull();

        MyJdbcTemplate myJdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        Assertions.assertThat(myJdbcTemplate).isNotNull();
        Assertions.assertThat(myJdbcTemplate.getDataSource()).isNotNull();
    }

}
