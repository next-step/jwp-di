package core.di.context;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import next.model.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import support.test.DBInitializer;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("@Configuration 기반 설정 테스트")
class AnnotationConfigApplicationContextTest {

    @BeforeEach
    void setUp() {
        DBInitializer.initialize();
    }

    @DisplayName("ApplicationContext에서 스캔한 Bean을 잘 읽어오는지 검사한다.")
    @Test
    void 이_테스트가_통과하면_편안해질_수_있을_것입니다() {
        final ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        assertThat(ctx.getBean(DataSource.class)).isNotNull();
        final JdbcTemplate jdbcTemplate = ctx.getBean(JdbcTemplate.class);
        assertThat(jdbcTemplate).isNotNull();
        final String sql = "SELECT * FROM USERS WHERE userId='admin'";
        final User user = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new User(
                        rs.getString("userId"), rs.getString("password"),
                        rs.getString("name"), rs.getString("email"))
        );
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("자바지기");
    }

    @Configuration
    @ComponentScan(basePackages = {"core", "next"})
    public static class ApplicationConfig {

        @Bean
        public DataSource getDataSource() {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem://localhost/~/jwp-jdbc;DB_CLOSE_DELAY=-1");
            ds.setUsername("sa");
            ds.setPassword("");
            return ds;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }
    }
}