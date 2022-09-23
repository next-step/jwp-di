package core.di.factory;

import core.di.factory.container.BeanFactory;
import core.di.factory.scanner.ConfigurationBeanScanner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("설정 빈 스캐너")
class ConfigurationBeanScannerTest {

    @DisplayName("설정된 빈들을 등록하고 조회시, 조회 성공")
    @Test
    void register_simple() {
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner("core.di.factory.example");

        BeanFactory beanFactory = new BeanFactory(cbs.scan());
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }
}
