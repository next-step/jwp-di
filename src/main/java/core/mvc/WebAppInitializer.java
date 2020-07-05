package core.mvc;

import core.di.factory.BeanScanner;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {
    private static BeanScanner beanScanner = new BeanScanner("next");

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(beanScanner));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }
}
