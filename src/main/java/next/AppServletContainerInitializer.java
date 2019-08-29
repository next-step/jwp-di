package next;

import core.di.factory.BeanFactory;
import core.di.factory.ClassPathBeanDefinitionScanner;
import core.mvc.DispatcherServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

@HandlesTypes(AppInitializer.class)
public class AppServletContainerInitializer implements ServletContainerInitializer {
    private static final Logger logger = LoggerFactory.getLogger(AppServletContainerInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext ctx) throws ServletException {
        logger.debug("onStartup {}", this.getClass().getName());
        BeanFactory beanFactory = new BeanFactory();
        ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanFactory);
        classPathBeanDefinitionScanner.loadBeanDefinitions("next.controller");
        beanFactory.initialize();

        ServletRegistration.Dynamic dispatcher =
                ctx.addServlet("dispatcher", new DispatcherServlet(beanFactory));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }


}
