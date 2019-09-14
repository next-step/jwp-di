package next.support.context;

import core.di.factory.ApplicationContext;
import core.mvc.DispatcherServlet;
import next.configuration.AppConfiguration;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class WebApplicationInitializer implements ApplicationInitializer {
    @Override
    public void onStartup(ServletContext ctx) {
        ApplicationContext applicationContext = new ApplicationContext(AppConfiguration.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext.initialize());

        ServletRegistration.Dynamic servletRegistration = ctx.addServlet("dispatcher", dispatcherServlet);
        servletRegistration.addMapping("/");
        servletRegistration.setLoadOnStartup(1);
    }
}
