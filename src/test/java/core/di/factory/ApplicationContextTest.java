package core.di.factory;

import next.configuration.AppConfiguration;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApplicationContextTest {
    @Test
    void createApplicationContext() {
        ApplicationContext ctx = new ApplicationContext(AppConfiguration.class);
        BeanFactory beanFactory = ctx.initialize();
        assertNotNull(ctx);
        assertNotNull(beanFactory.getBean(DataSource.class));
    }
}
