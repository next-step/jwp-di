package core.mvc;

import core.di.factory.BeanFactory;
import core.di.scanner.ClasspathBeanScanner;
import core.di.scanner.ConfigurationBeanScanner;
import core.mvc.asis.ControllerHandlerAdapter;
import core.mvc.asis.RequestMapping;
import core.mvc.tobe.AnnotationHandlerMapping;
import core.mvc.tobe.HandlerExecutionHandlerAdapter;
import next.MyConfiguration;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class WebApplicationInitializerImpl implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(MyConfiguration.class);
        beanFactory.initialize();

        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
        classpathBeanScanner.doScan(configurationBeanScanner.getBasePackages());

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.addHandlerMapping(new RequestMapping());
        dispatcherServlet.addHandlerMapping(new AnnotationHandlerMapping(classpathBeanScanner, beanFactory));

        dispatcherServlet.addHandlerAdapter(new HandlerExecutionHandlerAdapter());
        dispatcherServlet.addHandlerAdapter(new ControllerHandlerAdapter());

        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        registration.addMapping("/");
        registration.setLoadOnStartup(1);
    }
}
