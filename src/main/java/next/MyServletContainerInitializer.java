package next;

import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.mvc.DispatcherServlet;
import core.mvc.HandlerAdapterRegistry;
import core.mvc.HandlerExecutor;
import core.mvc.HandlerMappingRegistry;
import core.mvc.asis.ControllerHandlerAdapter;
import core.mvc.asis.RequestMapping;
import core.mvc.tobe.AnnotationHandlerMapping;
import core.mvc.tobe.HandlerExecutionHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

public class MyServletContainerInitializer implements ServletContainerInitializer {
    private static final Logger logger = LoggerFactory.getLogger(MyServletContainerInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        logger.info("MyServletContainerInitializer - onStartup");

        BeanScanner beanScanner = new BeanScanner("next.controller");
        BeanFactory beanFactory = new BeanFactory(beanScanner.getPreInitiatedClasses());
        beanFactory.initialize();

        HandlerMappingRegistry handlerMappingRegistry = new HandlerMappingRegistry();
        handlerMappingRegistry.addHandlerMpping(new RequestMapping());
        handlerMappingRegistry.addHandlerMpping(new AnnotationHandlerMapping(beanFactory));

        HandlerAdapterRegistry handlerAdapterRegistry = new HandlerAdapterRegistry();
        handlerAdapterRegistry.addHandlerAdapter(new HandlerExecutionHandlerAdapter());
        handlerAdapterRegistry.addHandlerAdapter(new ControllerHandlerAdapter());

        HandlerExecutor handlerExecutor = new HandlerExecutor(handlerAdapterRegistry);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(handlerMappingRegistry, handlerAdapterRegistry, handlerExecutor);

        ServletRegistration.Dynamic servletRegistration = ctx.addServlet("dispatcher", dispatcherServlet    );
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.addMapping("/");

    }
}
