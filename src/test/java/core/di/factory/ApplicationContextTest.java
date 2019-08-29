package core.di.factory;

import core.di.factory.example.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApplicationContextTest {

    @Test
    @DisplayName("@Bean 설정 단독 사용")
    void applicationContextWithExample() {
        ApplicationContext applicationContext = new ApplicationContext(ExampleConfig.class);
        final DataSource dataSource = applicationContext.getBean(DataSource.class);
        assertNotNull(dataSource);
        assertThat(dataSource).isInstanceOf(DataSource.class);
    }

    @Test
    @DisplayName("@Bean 2개 사용 (주입)")
    void applicationContextWithIntegration() throws SQLException {
        ApplicationContext applicationContext = new ApplicationContext(IntegrationConfig.class);
        final MyJdbcTemplate myJdbcTemplate = applicationContext.getBean(MyJdbcTemplate.class);
        assertNotNull(myJdbcTemplate);
        assertNotNull(myJdbcTemplate.getDataSource());
        assertNotNull(myJdbcTemplate.getDataSource().getConnection());
    }

    @Test
    @DisplayName("@Bean 2개(Configuration) + @Repository(ComponentScan) 1개 사용(각각 주입)")
    void configurationAndComponentScan() {
        ApplicationContext applicationContext = new ApplicationContext(ScanIntegrationConfig.class);

        final TestRepository testRepository = applicationContext.getBean(TestRepository.class);
        assertNotNull(testRepository);
        assertNotNull(testRepository.getJdbcTemplate());
        assertNotNull(testRepository.getJdbcTemplate().getDataSource());
    }

}
