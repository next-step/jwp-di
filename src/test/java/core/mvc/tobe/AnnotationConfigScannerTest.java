package core.mvc.tobe;

import core.di.factory.BeanFactory;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.MyJdbcTemplate;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class AnnotationConfigScannerTest {
    @Test
    void name() {
        BeanFactory beanFactory = new BeanFactory();
        AnnotationConfigScanner annotationConfigScanner = new AnnotationConfigScanner(beanFactory);
        annotationConfigScanner.loadConfig(IntegrationConfig.class);

        DataSource dataSource = beanFactory.getBean(DataSource.class);
        MyJdbcTemplate myJdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);

        assertThat(dataSource).isEqualTo(myJdbcTemplate.getDataSource());
    }
}
