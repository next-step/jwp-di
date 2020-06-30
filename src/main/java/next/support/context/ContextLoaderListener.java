package next.support.context;

import core.di.ApplicationContext;
import core.mvc.DispatcherServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // order is important
        ApplicationContext appContext = new ApplicationContext();
        initDatabase(appContext);
        initDispatcherServlet(sce, appContext);

        logger.info("Completed Load ServletContext!");
    }

    private void initDispatcherServlet(ServletContextEvent sce, ApplicationContext appContext) {
        ServletRegistration.Dynamic dispatcher =
                sce.getServletContext().addServlet("dispatcher", new DispatcherServlet(appContext));
        dispatcher.addMapping("/");
        dispatcher.setLoadOnStartup(1);
    }

    private void initDatabase(ApplicationContext appContext) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, appContext.getBean(DataSource.class));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
