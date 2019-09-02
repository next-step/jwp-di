package core.context.annotation;

import core.context.ApplicationContext;
import core.web.config.MyConfiguration;
import next.controller.ApiQnaController;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnnotationConfigApplicationContextTest {

    @Test
    void getBeans() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);

        DataSource dataSourceBean = applicationContext.getBean(DataSource.class);
        JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);

        ApiQnaController controller = applicationContext.getBean(ApiQnaController.class);


        assertNotNull(dataSourceBean);
        assertNotNull(jdbcTemplate);
        assertNotNull(controller);
    }
}