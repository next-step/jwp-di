package core.di.factory;

import core.di.factory.example.IntegrationConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeanFactoryUtilsTest {

    @Test
    void getBasePackage() {
        Object[] basePackages = BeanFactoryUtils.getBasePackages(IntegrationConfig.class);
        assertThat(basePackages).contains("core.di.factory","next");
    }
}