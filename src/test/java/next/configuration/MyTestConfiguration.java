package next.configuration;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"core.mvc.tobe", "core.di.factory.example"})
public class MyTestConfiguration {
    @Bean
    public DataSource dataSource() {
        return new BasicDataSource();
    }
}
