package core.di;

import static org.assertj.core.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import core.di.factory.BeanFactory;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;

class AnnotationConfigApplicationContextTest {

    private BeanFactory beanFactory;

    @Test
    void scan() {
        beanFactory = new AnnotationConfigApplicationContext(IntegrationConfig.class);

        assertThat(beanFactory.getBean(IntegrationConfig.class)).isNotNull();
        assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
        assertThat(beanFactory.getBean(MyJdbcTemplate.class)).isNotNull();
    }

    @Test
    void di() {
        beanFactory = new AnnotationConfigApplicationContext("core.di");
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertThat(qnaController).isNotNull();
        assertThat(qnaController.getQnaService()).isNotNull();

        MyQnaService qnaService = qnaController.getQnaService();
        assertThat(qnaService.getUserRepository()).isNotNull();
        assertThat(qnaService.getQuestionRepository()).isNotNull();
    }
}
