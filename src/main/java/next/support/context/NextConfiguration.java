package next.support.context;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.jdbc.JdbcTemplate;
import core.jdbc.JdbcTemplate2;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

@Configuration
@ComponentScan({ "next", "core" })
public class NextConfiguration {

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
    public JdbcTemplate2 jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate2(dataSource);
    }

}
