package core.di;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.Sets;
import core.di.factory.BeanFactory;
import core.di.factory.BeanFactoryUtils;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import core.util.ReflectionUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class ConfigurationBeanScannerTest {
    @Test
    public void register_simple() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(ExampleConfig.class);

        beanFactory.addBeanDefinition(new BeanDefinition() {
            @Override
            public String getName() {
                return ExampleConfig.class.getSimpleName();
            }

            @Override
            public Method getMethod() {
                return null;
            }

            @Override
            public Constructor getConstructor() {
                return BeanFactoryUtils.getInjectedConstructor(ExampleConfig.class)
                    .orElseGet(() -> ReflectionUtils.getConstructorByArgs(ExampleConfig.class));
            }

            @Override
            public Class<?> getBeanClass() {
                return ExampleConfig.class;
            }
        });
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }

    @Test
    public void register_classpathBeanScanner_통합() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(IntegrationConfig.class);

        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanFactory);
        cbds.doScan(Sets.newHashSet("core.di"));

        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);

        MyJdbcTemplate jdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate);
        assertNotNull(jdbcTemplate.getDataSource());
    }
}
