package core.di.factory;

import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.MyJdbcTemplate;
import core.jdbc.JdbcTemplate;
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
        applicationContext.refresh();
        final DataSource dataSource = applicationContext.getBean(DataSource.class);
        assertNotNull(dataSource);
        assertThat(dataSource).isInstanceOf(DataSource.class);
    }

    @Test
    @DisplayName("@Bean 2개 사용 (주입)")
    void applicationContextWithIntegration() throws SQLException {
        ApplicationContext applicationContext = new ApplicationContext(IntegrationConfig.class);
        applicationContext.refresh();
        final MyJdbcTemplate myJdbcTemplate = applicationContext.getBean(MyJdbcTemplate.class);
        assertNotNull(myJdbcTemplate);
        assertNotNull(myJdbcTemplate.getDataSource());
        assertNotNull(myJdbcTemplate.getDataSource().getConnection());

    }
}
