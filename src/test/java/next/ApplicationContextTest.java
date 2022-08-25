package next;

import core.annotation.Bean;
import core.config.WebMvcConfiguration;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationContextTest {


    @Test
    public void register_classpathBeanScanner_통합() {
        ApplicationContext ac = new ApplicationContext(IntegrationConfig.class);
        ac.initialize();

        assertNotNull(ac.getBean(DataSource.class));

        JdbcUserRepository userRepository = ac.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);

        MyJdbcTemplate jdbcTemplate = ac.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate);
        assertNotNull(jdbcTemplate.getDataSource());
    }


    @Test
    void initialize() {
        ApplicationContext ac = new ApplicationContext(AConfig.class);
        ac.initialize();

        assertThat(ac.getBean(String.class)).isEqualTo("a");
        assertThat(ac.getBean(Integer.class)).isEqualTo(5);
    }

    public static class AConfig implements WebMvcConfiguration {

        @Bean
        public String a() {
            return "a";
        }

        @Bean
        public Integer b(String a) {
            return 5;
        }
    }
}
