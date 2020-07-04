package next.support.context;

import core.di.factory.ApplicationContext;
import core.jdbc.ConnectionManager;
import core.mvc.DispatcherServlet;
import core.mvc.asis.ControllerHandlerAdapter;
import core.mvc.asis.RequestMapping;
import core.mvc.tobe.AnnotationHandlerMapping;
import core.mvc.tobe.HandlerExecutionHandlerAdapter;
import next.config.MyConfiguration;
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

    public static ApplicationContext applicationContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        applicationContext = new ApplicationContext(MyConfiguration.class);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, applicationContext.getBean(DataSource.class));

/*
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
*/

        logger.info("Completed Load ServletContext!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
