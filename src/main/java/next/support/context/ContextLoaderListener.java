package next.support.context;

import core.annotation.ComponentScan;
import core.di.factory.BeanFactory;
import core.di.factory.BeanFactoryUtils;
import core.di.factory.ComponentScanner;
import core.jdbc.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initDatabase();
        initBeans();

        logger.info("Completed Load ServletContext!");
    }

    private void initDatabase() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
    }

    private void initBeans() {
        Set<Class<?>> classes = ComponentScanner.scan(getBasePackage());
        BeanFactory beanFactory = new BeanFactory(classes);
        beanFactory.initialize();

        BeanFactoryUtils.setBeanFactory(beanFactory);
    }

    private String[] getBasePackage() {
        Set<Class<?>> classes =
                ComponentScanner.scan(Collections.singletonList(ComponentScan.class), "");

        return classes.stream()
                .map(clazz -> clazz.getDeclaredAnnotation(ComponentScan.class))
                .map(ComponentScan::basePackages)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
