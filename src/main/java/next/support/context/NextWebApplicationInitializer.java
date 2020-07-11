package next.support.context;

import core.di.context.AnnotationConfigApplicationContext;
import core.di.context.ApplicationContext;
import core.jdbc.ConnectionManager;
import core.mvc.DispatcherServlet;
import core.mvc.asis.ControllerHandlerAdapter;
import core.mvc.asis.RequestMapping;
import core.mvc.tobe.AnnotationHandlerMapping;
import core.mvc.tobe.HandlerExecutionHandlerAdapter;
import core.web.WebApplicationInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;
import next.config.NextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

public class NextWebApplicationInitializer implements WebApplicationInitializer {

    private static final Logger logger = LoggerFactory.getLogger(NextWebApplicationInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        ApplicationContext ac = new AnnotationConfigApplicationContext(NextConfiguration.class);

        initDatabase(ac.getBean(DataSource.class));

        DispatcherServlet servlet = new DispatcherServlet(ac);
        servlet.addHandlerMapping(new RequestMapping());
        servlet.addHandlerMapping(new AnnotationHandlerMapping(ac));
        servlet.addHandlerAdapter(new HandlerExecutionHandlerAdapter());
        servlet.addHandlerAdapter(new ControllerHandlerAdapter());

        ServletRegistration sr = servletContext.addServlet("dispacher", servlet);
        sr.addMapping("/");
        logger.info("Completed Load ServletContext!");
    }

    private void initDatabase(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}
