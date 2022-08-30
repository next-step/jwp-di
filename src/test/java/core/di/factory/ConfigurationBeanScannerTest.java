package core.di.factory;

import core.di.factory.example.MyJdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("설정 빈 스캐너")
class ConfigurationBeanScannerTest {

    @Test
    @DisplayName("설정된 빈들 등록")
    void initializeByScan() {
        //given
        ConfigurationBeanScanner configurationBeanScanner = ConfigurationBeanScanner.packages("core.di.factory.example");
        //when
        BeanFactory beanFactory = BeanFactory.from(configurationBeanScanner.scan());
        beanFactory.initialize();
        //then
        assertAll(
                () -> assertNotNull(beanFactory.getBean(DataSource.class)),
                () -> assertNotNull(beanFactory.getBean(MyJdbcTemplate.class)),
                () -> assertNotNull(beanFactory.getBean(MyJdbcTemplate.class).getDataSource())
        );
    }
}
