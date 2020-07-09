package core.mvc;

import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        BeanScanner beanScanner = new BeanScanner();
        BeanFactory beanFactory = new BeanFactory(beanScanner.getBeanAdapters());

        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(beanFactory));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }
}
