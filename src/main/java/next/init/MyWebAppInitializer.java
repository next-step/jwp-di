package next.init;

import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.mvc.DispatcherServlet;
import core.mvc.asis.ControllerHandlerAdapter;
import core.mvc.asis.RequestMapping;
import core.mvc.tobe.AnnotationHandlerMapping;
import core.mvc.tobe.HandlerExecutionHandlerAdapter;
import core.web.WebApplicationInitializer;

import javax.servlet.ServletContext;

public class MyWebAppInitializer implements WebApplicationInitializer {

    private static final String DEFAULT_SCAN_PACKAGE = "next";

    @Override
    public void onStartup(ServletContext servletContext) {
        BeanFactory beanFactory = beanInitializer();

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.addHandlerMapping(new RequestMapping());
        dispatcherServlet.addHandlerMapping(new AnnotationHandlerMapping(beanFactory));

        dispatcherServlet.addHandlerAdapter(new HandlerExecutionHandlerAdapter());
        dispatcherServlet.addHandlerAdapter(new ControllerHandlerAdapter());
    }

    private BeanFactory beanInitializer() {
        BeanScanner beanScanner = new BeanScanner(DEFAULT_SCAN_PACKAGE);
        return BeanFactory.initialize(beanScanner.enroll());
    }
}
