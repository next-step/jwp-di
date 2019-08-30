package core.di.factory;

import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.MyJdbcTemplate;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationClassPathBeanDefinitionScannerTest {

    @Test
    public void scan() throws Exception {
        AnnotatedBeanDefinitionReader scanner = new AnnotatedBeanDefinitionReader(IntegrationConfig.class);
        BasicDataSource dataSource = scanner.getBean(BasicDataSource.class);
        MyJdbcTemplate myJdbcTemplate = scanner.getBean(MyJdbcTemplate.class);

        assertNotNull(dataSource);
        assertThat(dataSource.getDriverClassName()).isEqualTo("org.h2.Driver");
        assertNotNull(myJdbcTemplate);
        assertThat(myJdbcTemplate.getDataSource()).isInstanceOf(BasicDataSource.class);
    }
}
