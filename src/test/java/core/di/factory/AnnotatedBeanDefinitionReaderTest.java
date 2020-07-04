package core.di.factory;

import core.di.beans.definition.reader.AnnotatedBeanDefinitionReader;
import core.di.beans.definition.reader.ClasspathBeanDefinitionReader;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AnnotatedBeanDefinitionReaderTest {

    @Test
    public void register_simple() {
        BeanFactory beanFactory = new BeanFactory();
        AnnotatedBeanDefinitionReader abdr = new AnnotatedBeanDefinitionReader(beanFactory);
        abdr.read(ExampleConfig.class);
        beanFactory.instantiateBeans();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }

    @Test
    public void register_classpathBeanScanner_통합() {
        BeanFactory beanFactory = new BeanFactory();
        AnnotatedBeanDefinitionReader abdr = new AnnotatedBeanDefinitionReader(beanFactory);
        abdr.read(ExampleConfig.class);

        ClasspathBeanDefinitionReader cbdr = new ClasspathBeanDefinitionReader(beanFactory);
        cbdr.doScan("core.di.factory.example");
        beanFactory.instantiateBeans();

        assertNotNull(beanFactory.getBean(DataSource.class));

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);
        assertNotNull(userRepository.getDataSource());

        MyJdbcTemplate jdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate);
        assertNotNull(jdbcTemplate.getDataSource());
    }
}
