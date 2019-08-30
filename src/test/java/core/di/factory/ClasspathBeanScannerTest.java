package core.di.factory;

import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClasspathBeanScannerTest {

    private SimpleBeanFactory beanFactory;

    @BeforeEach
    void setUp() {
        beanFactory = new SimpleBeanFactory();
    }

    @Test
    void classpathBeanScanner() {
        ClasspathBeanScanner beanScanner = new ClasspathBeanScanner(beanFactory);
        beanScanner.scan("core.di.factory.example");
        beanFactory.initialize();
        assertNotNull(beanFactory.getBean(QnaController.class));
    }

    @Test
    void register_classpathBeanScanner_통합() {
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.scan(IntegrationConfig.class);
        beanFactory.initialize();

        ClasspathBeanScanner cpbs = new ClasspathBeanScanner(beanFactory);
        cpbs.scan("core.di.factory.example");
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);
        assertNotNull(userRepository.getDataSource());

        MyJdbcTemplate jdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate);
        assertNotNull(jdbcTemplate.getDataSource());
    }

}
