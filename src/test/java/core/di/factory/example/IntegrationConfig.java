package core.di.factory.example;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.annotation.Qualifier;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

@Configuration
public class IntegrationConfig {

    @Bean
    public DataSource dataSource2() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:~/jwp-basic;AUTO_SERVER=TRUE");
        ds.setUsername("kingcjy");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public MyJdbcTemplate jdbcTemplate(@Qualifier("dataSource2") DataSource dataSource) {
        return new MyJdbcTemplate(dataSource);
    }
}
