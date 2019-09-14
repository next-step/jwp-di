package core.di.factory;

import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.mvc.tobe.AnnotationScanner;
import core.mvc.tobe.ConfigurationScanner;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanFactoryTest {

    @Test
    void component_di() {
        AnnotationScanner annotationScanner = new AnnotationScanner("core.di.factory.example");

        BeanFactory beanFactory = new BeanFactory(annotationScanner);
        beanFactory.initialize();

        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @Test
    void configuration_di() {
        ConfigurationScanner configurationScanner = new ConfigurationScanner("core.di.factory.example");

        BeanFactory beanFactory = new BeanFactory(configurationScanner);
        beanFactory.initialize();

        MyJdbcTemplate jdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate);
        assertThat(jdbcTemplate).isInstanceOf(MyJdbcTemplate.class);

        DataSource dataSource = beanFactory.getBean(BasicDataSource.class);
        assertNotNull(dataSource);
        assertThat(dataSource).isInstanceOf(BasicDataSource.class);
    }

    @Test
    void di() {
        AnnotationScanner annotationScanner = new AnnotationScanner("core.di.factory.example");
        ConfigurationScanner configurationScanner = new ConfigurationScanner("core.di.factory.example");

        BeanFactory beanFactory = new BeanFactory(annotationScanner, configurationScanner);
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(MyQnaService.class));
        assertNotNull(beanFactory.getBean(MyJdbcTemplate.class));
    }
}
