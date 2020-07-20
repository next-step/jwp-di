package core.di.factory;

import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationTest.class);

    private Reflections reflections;
    private Set<Class<?>> configurationClasses;
    private BeanFactory beanFactory;
    private ConfigurationBeanScanner configurationBeanScanner;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        configurationBeanScanner = new ConfigurationBeanScanner();
        this.configurationClasses = configurationBeanScanner.scan();
        beanFactory = new BeanFactory(configurationClasses);
        beanFactory.initialize();
        beanFactory.instantiateConfiguration(configurationBeanScanner.scan());

    }

    @Test
    public void scanConfiguration() {
        configurationClasses.contains(ExampleConfig.class);
    }

    @Test
    public void addConfigurationBean() throws Exception {
        Class<?> configurationClass = ExampleConfig.class;
        Method method = configurationClass.getMethod("dataSource");
        Object dataSource = beanFactory.getBean(method.getReturnType());
        assertThat(dataSource).isNotEqualTo(null);
    }

    @Test
    public void addConfigurationBean2() throws Exception {
        Class<?> configurationClass = IntegrationConfig.class;
        Method method = configurationClass.getMethod("jdbcTemplate", DataSource.class);
        Object dataSource = beanFactory.getBean(method.getReturnType());
        assertThat(dataSource).isNotEqualTo(null);
    }
}
