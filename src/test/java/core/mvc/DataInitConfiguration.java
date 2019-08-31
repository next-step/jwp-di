package core.mvc;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import core.annotation.Configuration;
import core.jdbc.ConnectionManager;

@Configuration
public class DataInitConfiguration {
	
	public DataInitConfiguration() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
	    populator.addScript(new ClassPathResource("jwp.sql"));
	    DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
	}
}
