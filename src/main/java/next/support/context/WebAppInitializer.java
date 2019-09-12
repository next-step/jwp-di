package next.support.context;

import core.di.factory.ApplicationContext;
import core.mvc.DispatcherServlet;
import next.configuration.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

public class WebAppInitializer implements ServletContainerInitializer {
    private static final Logger logger = LoggerFactory.getLogger(WebAppInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> configs, ServletContext ctx) throws ServletException {
        ApplicationContext applicationContext = new ApplicationContext(AppConfiguration.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext.initialize());

        ServletRegistration.Dynamic servletRegistration = ctx.addServlet("dispatcher", dispatcherServlet);
        servletRegistration.addMapping("/");
        servletRegistration.setLoadOnStartup(1);
    }
}
