package core.di.factory;

import next.configuration.AppConfiguration;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApplicationContextTest {
    @Test
    void createApplicationContext() {
        ApplicationContext ac = new ApplicationContext(AppConfiguration.class);
        BeanFactory beanFactory = ac.initialize();
        assertNotNull(ac);
        assertNotNull(beanFactory.getBean(DataSource.class));
    }
}
