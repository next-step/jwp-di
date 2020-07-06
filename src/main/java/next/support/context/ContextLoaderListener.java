package next.support.context;

import core.annotation.Component;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.BeanScanner;
import core.di.factory.BeanFactory;
import core.jdbc.ConnectionManager;
import core.mvc.DispatcherServlet;
import javax.servlet.ServletRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());

        logger.info("Completed Load ServletContext!");

        BeanScanner beanScanner = new BeanScanner("next");
        BeanFactory beanFactory = new BeanFactory(beanScanner.scan(Component.class));
        beanFactory.initialize();

        DispatcherServlet servlet = new DispatcherServlet(beanFactory);
        ServletRegistration sr = sce.getServletContext().addServlet("dispacher", servlet);
        sr.addMapping("/");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
