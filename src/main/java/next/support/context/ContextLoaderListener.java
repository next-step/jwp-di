package next.support.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import core.di.scanner.BeanScanner;
import core.jdbc.ConnectionManager;
import core.mvc.DispatcherServlet;
import core.mvc.asis.ControllerHandlerAdapter;
import core.mvc.asis.RequestMapping;
import core.mvc.tobe.AnnotationHandlerMapping;
import core.mvc.tobe.HandlerExecutionHandlerAdapter;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initDatabase();
        initWeb(sce.getServletContext());
        logger.info("Completed Load ServletContext!");
    }

    private void initDatabase() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
    }

    private void initWeb(ServletContext servletContext) {
        BeanScanner beanScanner = new BeanScanner("next.config");
        beanScanner.scan();
        beanScanner.beanInitialize();

        DispatcherServlet dispatcherServlet = new DispatcherServlet();

        dispatcherServlet.addHandlerMapping(new RequestMapping());
        dispatcherServlet.addHandlerMapping(new AnnotationHandlerMapping(beanScanner));
        dispatcherServlet.addHandlerAdapter(new HandlerExecutionHandlerAdapter());
        dispatcherServlet.addHandlerAdapter(new ControllerHandlerAdapter());

        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
