package core.di.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.di.factory.example.ExampleConfig;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Created by iltaek on 2020/07/19 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class ConfigurationBeanScannerTest {

    @DisplayName("Configuration Class로 설정된 Bean 스캔 테스트")
    @Test
    void registerBeansWithConfigClassTest() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanDefinitionScanner cbds = new ConfigurationBeanDefinitionScanner(beanFactory);
        cbds.register(ExampleConfig.class);
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }
}
