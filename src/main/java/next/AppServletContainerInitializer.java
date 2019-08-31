package next;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.di.factory.ApplicationContext;
import core.mvc.DispatcherServlet;

@HandlesTypes(AppInitializer.class)
public class AppServletContainerInitializer implements ServletContainerInitializer {
    private static final Logger logger = LoggerFactory.getLogger(AppServletContainerInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext ctx) throws ServletException {
        logger.debug("onStartup {}", this.getClass().getName());
        
        ApplicationContext applicationContext = new ApplicationContext(AppConfiguration.class);

        ServletRegistration.Dynamic dispatcher =
                ctx.addServlet("dispatcher", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }


}
