package next.support.context;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
<<<<<<< HEAD
=======
import core.di.factory.AnnotationApplicationContext;
>>>>>>> step3
import core.di.factory.ApplicationContext;
import core.jdbc.ConnectionManager;
import core.jdbc.JdbcTemplate;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
@Configuration
@ComponentScan
public class ContextLoaderListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ContextLoaderListener.class);

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:~/jwp-framework;MVCC=TRUE;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
<<<<<<< HEAD
        ApplicationContext applicationContext = new ApplicationContext(this.getClass());
=======
        ApplicationContext applicationContext = new AnnotationApplicationContext(this.getClass());
>>>>>>> step3

        sce.getServletContext().setAttribute(ApplicationContext.class.getName(), applicationContext);
        logger.info("Completed Load ServletContext!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
