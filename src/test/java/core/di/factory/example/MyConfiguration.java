package core.di.factory.example;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan({ "core.di.factory.example" })
public class MyConfiguration {
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
    public MyJdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new MyJdbcTemplate(dataSource);
    }
}
