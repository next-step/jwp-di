package core.di.factory;

import core.jdbc.JdbcTemplate;
import next.MyConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationBeanScannerTest {
    @Test
    void getBean() {
        BeanFactory beanFactory = new BeanFactory();
        AnnotationBeanScanner scanner = new AnnotationBeanScanner(MyConfiguration.class);
        scanner.scan(beanFactory);
        beanFactory.initialize();

        assertThat(beanFactory.getBean(JdbcTemplate.class)).isInstanceOf(JdbcTemplate.class);
    }

}