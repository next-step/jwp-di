package next.config;

import static org.assertj.core.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import core.di.factory.BeanFactory;
import core.di.factory.example.QnaController;
import core.jdbc.JdbcTemplate;
import next.controller.ApiQnaController;
import next.controller.ApiUserController;
import next.controller.HomeController;
import next.controller.UserController;

class MyConfigurationTest {

    @Test
    void scan() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner scanner = new ConfigurationBeanScanner(beanFactory);
        scanner.register(MyConfiguration.class);
        beanFactory.initialize();

        assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
        assertThat(beanFactory.getBean(JdbcTemplate.class)).isNotNull();

        // next 및 하위 패키지
        assertThat(beanFactory.getBean(ApiQnaController.class)).isNotNull();
        assertThat(beanFactory.getBean(ApiUserController.class)).isNotNull();
        assertThat(beanFactory.getBean(HomeController.class)).isNotNull();
        assertThat(beanFactory.getBean(QnaController.class)).isNotNull();
        assertThat(beanFactory.getBean(UserController.class)).isNotNull();
    }
}
