package core.di.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AnnotationConfigurationMappingTest {

    @Test
    void configurationObjectCountTest() {
        AnnotationConfigurationMapping annotationConfigurationMapping = new AnnotationConfigurationMapping();
        int size = annotationConfigurationMapping.getTypesAnnotatedWith().size();
        /**
         * core.di.config.MyConfiguration
         * core.di.factory.example.IntegrationConfig
         * core.di.factory.example.ExampleConfig
         */
        assertThat(size).isEqualTo(3);
    }
}
