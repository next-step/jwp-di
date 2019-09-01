package core.mvc;

import core.di.factory.BeanFactory;
import core.mvc.asis.ControllerHandlerAdapter;
import core.mvc.asis.RequestMapping;
import core.mvc.tobe.AnnotationHandlerMapping;
import core.mvc.tobe.BeanScanner;
import core.mvc.tobe.HandlerExecutionHandlerAdapter;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

public class WebAppInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        BeanScanner beanScanner = new BeanScanner(new String[]{""});
        BeanFactory beanFactory = new BeanFactory(beanScanner.getAllBeanClasses());
        beanFactory.initialize();

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.addHandlerMapping(new RequestMapping());
        dispatcherServlet.addHandlerMapping(new AnnotationHandlerMapping(beanScanner, beanFactory));

        dispatcherServlet.addHandlerAdapter(new HandlerExecutionHandlerAdapter());
        dispatcherServlet.addHandlerAdapter(new ControllerHandlerAdapter());

        ServletRegistration.Dynamic registration = ctx.addServlet("dispatcherServlet", dispatcherServlet);
        registration.addMapping("/");
        registration.setLoadOnStartup(1);
    }
}
