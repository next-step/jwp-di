package next;

import core.di.factory.ApplicationContext;
import core.mvc.DispatcherServlet;
import core.mvc.asis.ControllerHandlerAdapter;
import core.mvc.asis.RequestMapping;
import core.mvc.tobe.AnnotationHandlerMapping;
import core.mvc.tobe.HandlerExecutionHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class MyAppInitializer implements AppInitializer {
    private static final Logger logger = LoggerFactory.getLogger(MyAppInitializer.class);

    @Override
    public void onStartup(ServletContext ctx) throws ServletException {

        logger.debug("onStartup {}", this.getClass().getName());

        ApplicationContext applicationContext = new ApplicationContext(AppConfiguration.class);

        DispatcherServlet dispatcherServlet = applicationContext.getBean(DispatcherServlet.class);

        dispatcherServlet.addHandlerMapping(new RequestMapping());
        dispatcherServlet.addHandlerMapping(new AnnotationHandlerMapping(applicationContext));
        dispatcherServlet.addHandlerAdapter(new HandlerExecutionHandlerAdapter());
        dispatcherServlet.addHandlerAdapter(new ControllerHandlerAdapter());

        ServletRegistration.Dynamic dispatcher = ctx.addServlet("dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
