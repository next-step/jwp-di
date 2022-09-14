package core.di.factory;

import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
public class ConfigurationBeanScannerTest {

    @Test
    void resister() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(IntegrationConfig.class);

        ClassPathBeanScanner classPathBeanScanner = new ClassPathBeanScanner(beanFactory);
        classPathBeanScanner.doScan("core.di.factory");

        beanFactory.initialize();

        Assertions.assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        Assertions.assertThat(userRepository).isNotNull();

        MyJdbcTemplate myJdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        Assertions.assertThat(myJdbcTemplate).isNotNull();
        Assertions.assertThat(myJdbcTemplate.getDataSource()).isNotNull();
    }

}
