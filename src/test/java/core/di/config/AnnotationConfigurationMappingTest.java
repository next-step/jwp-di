package core.di.config;

import core.annotation.ComponentScan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationConfigurationMappingTest {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationConfigurationMappingTest.class);

    private AnnotationConfigurationMapping annotationConfigurationMapping;

    @BeforeEach
    void setUp() {
        annotationConfigurationMapping = new AnnotationConfigurationMapping();
    }

    @Test
    void configurationObjectCountTest() {
        int size = annotationConfigurationMapping.getTypesAnnotatedWith().size();
        /**
         * core.di.config.MyConfiguration
         * core.di.factory.example.IntegrationConfig
         * core.di.factory.example.ExampleConfig
         */
        assertThat(size).isEqualTo(3);
    }

    @Test
    void getComponentScan() {
        Set<Class<?>> typesAnnotatedWith = annotationConfigurationMapping.getTypesAnnotatedWith();
        typesAnnotatedWith.forEach(clazz -> {
            logger.info(clazz.getName() + " : " + clazz.isAnnotationPresent(ComponentScan.class));
        });
    }

    @Test
    void getBeanByFactory() {
        annotationConfigurationMapping.scan();
        final Object bean = annotationConfigurationMapping.getBean(DataSource.class);
        assertThat(bean).isNotNull();
    }
}
