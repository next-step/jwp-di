package next;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.jdbc.JdbcTemplate;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

/**
 * Created by hspark on 2019-09-04.
 */

@Configuration
@ComponentScan({ "next", "core" })
public class NextStepConfiguration {
    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:~/jwp-framework;MVCC=TRUE;DB_CLOSE_ON_EXIT=FALSE");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
