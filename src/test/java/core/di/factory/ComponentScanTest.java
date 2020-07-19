package core.di.factory;

import core.di.factory.example.QnaController;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentScanTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationTest.class);

    @Test
    public void componentScan() {
        String[] basePackages = BeanScanner.getBasePackagesWithComponentScan();
        BeanFactory beanFactory = new BeanFactory(BeanScanner.scan(basePackages));
        beanFactory.initialize();
        Object obj = beanFactory.getBean(QnaController.class);
        assertThat(obj).isNotEqualTo(null);
    }
}
