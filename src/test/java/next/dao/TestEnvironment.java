package next.dao;

import core.di.factory.BeanFactory;
import core.jdbc.ConnectionManager;
import core.mvc.tobe.ClassPathBeanScanner;
import core.mvc.tobe.ConfigurationBeanScanner;
import next.config.TestConfiguration;
import next.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

public class TestEnvironment {

    protected BeanFactory beanFactory;

    protected void setUpConfig() {
        setUpDb();
        setUpBeans();
    }

    private void setUpDb() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
    }

    private void setUpBeans() {
        AnnotationConfigApplicationContext annotationConfigApplicationContext
            = new AnnotationConfigApplicationContext(TestConfiguration.class);

        this.beanFactory = annotationConfigApplicationContext.getBeanFactory();
    }

}
