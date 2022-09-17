package next.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.jdbc.JdbcTemplate;

@Configuration
@ComponentScan(basePackages = {"next", "core"})
public class DataSourceConfig {
	private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:mem://localhost/~/jwp-di;MODE=MySQL;DB_CLOSE_DELAY=-1";
	private static final String DB_USERNAME = "sa";
	private static final String DB_PW = "";

	@Bean
	public DataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(DB_DRIVER);
		ds.setUrl(DB_URL);
		ds.setUsername(DB_USERNAME);
		ds.setPassword(DB_PW);
		return ds;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
