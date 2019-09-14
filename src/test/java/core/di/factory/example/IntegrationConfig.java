package core.di.factory.example;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import org.apache.commons.dbcp2.managed.BasicManagedDataSource;

import javax.sql.DataSource;

@ComponentScan({"core", "next"})
@Configuration
public class IntegrationConfig {

    @Bean
    public DataSource dataSource() {
        BasicManagedDataSource ds = new BasicManagedDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:~/jwp-basic;AUTO_SERVER=TRUE");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public MyJdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new MyJdbcTemplate(dataSource);
    }
}
