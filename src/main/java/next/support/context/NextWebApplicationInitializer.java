package next.support.context;

import core.annotation.Component;
import core.di.BeanScanner;
import core.di.factory.BeanFactory;
import core.jdbc.ConnectionManager;
import core.mvc.DispatcherServlet;
import core.web.WebApplicationInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

public class NextWebApplicationInitializer implements WebApplicationInitializer {

    private static final Logger logger = LoggerFactory.getLogger(NextWebApplicationInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());

        logger.info("Completed Load ServletContext!");

        BeanScanner beanScanner = new BeanScanner("next");
        BeanFactory beanFactory = new BeanFactory(beanScanner.scan(Component.class));
        beanFactory.initialize();

        DispatcherServlet servlet = new DispatcherServlet(beanFactory);
        ServletRegistration sr = servletContext.addServlet("dispacher", servlet);
        sr.addMapping("/");
    }
}
