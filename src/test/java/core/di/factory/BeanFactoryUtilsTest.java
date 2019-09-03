package core.di.factory;

import core.web.config.MyConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeanFactoryUtilsTest {

    @Test
    void getBasePackage() {
        Object[] basePackages = BeanFactoryUtils.getBasePackages(MyConfiguration.class);
        assertThat(basePackages).contains("core", "next");
    }
}