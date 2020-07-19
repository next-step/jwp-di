package core.di;

import core.di.factory.example.MyJdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationBeanScannerTest {

    @DisplayName("기준 package 하위에 있는 Configuration Bean Method 들을 스캔한다.")
    @Test
    void scan() {
        /* given */
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner("core.di.factory.example");

        /* when */
        Set<Class<?>> preInstantiateClasses = configurationBeanScanner.scan();

        /* then */
        assertThat(preInstantiateClasses).hasSize(2);
        assertThat(preInstantiateClasses).containsExactlyInAnyOrder(DataSource.class, MyJdbcTemplate.class);
    }

}
