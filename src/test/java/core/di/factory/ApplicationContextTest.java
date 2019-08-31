package core.di.factory;

import core.di.factory.example.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationContextTest {

    @Test
    @DisplayName("[Annotation] @Bean 설정 단독 사용")
    void applicationContextWithExample() {
        ApplicationContext applicationContext = new AnnotationApplicationContext(ExampleConfig.class);
        final DataSource dataSource = applicationContext.getBean(DataSource.class);
        assertNotNull(dataSource);
        assertThat(dataSource).isInstanceOf(DataSource.class);
    }

    @Test
    @DisplayName("[Annotation] @Bean 2개 사용 (주입)")
    void applicationContextWithIntegration() throws SQLException {
        ApplicationContext applicationContext = new AnnotationApplicationContext(IntegrationConfig.class);
        final MyJdbcTemplate myJdbcTemplate = applicationContext.getBean(MyJdbcTemplate.class);
        assertNotNull(myJdbcTemplate);
        assertNotNull(myJdbcTemplate.getDataSource());
        assertNotNull(myJdbcTemplate.getDataSource().getConnection());
    }

    @Test
    @DisplayName("[Annotation] @Bean 2개(Configuration) + @Repository(ComponentScan) 1개 사용(각각 주입)")
    void configurationAndComponentScan() {
        ApplicationContext applicationContext = new AnnotationApplicationContext(ScanIntegrationConfig.class);

        final TestRepository testRepository = applicationContext.getBean(TestRepository.class);
        assertNotNull(testRepository);
        assertNotNull(testRepository.getJdbcTemplate());
        assertNotNull(testRepository.getJdbcTemplate().getDataSource());
    }

    @Test
    @DisplayName("[Classpath] example 패키지 Controller-Service-Repository 테스트")
    void classpathExampleTest() {
        ApplicationContext applicationContext = new ClasspathApplicationContext("core.di.factory.example");

        final QnaController qnaController = applicationContext.getBean(QnaController.class);
        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());
        assertNotNull(qnaController.getQnaService().getUserRepository());
    }

}
