package core.di.factory;

import static org.assertj.core.api.Assertions.assertThat;

import core.annotation.Configuration;
import core.di.factory.example.MyJdbcTemplate;
import core.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

class ConfigurationBeanScannerTest {

    private Set<Class<?>> configurationClasses;
    private ConfigurationBeanScanner configurationBeanScanner;

    @BeforeEach
    void setUp() {
        configurationClasses = ReflectionUtils.getTypesAnnotatedWith(new Reflections("core.di.factory.example"), Configuration.class);
        configurationBeanScanner = new ConfigurationBeanScanner(configurationClasses);
    }

    @DisplayName("`@Configuration` 애너테이션이 적용된 클래스를 를 주입받아 ConfigurationBeanScanner 를 생성한다")
    @Test
    void constructor() {
        assertThat(configurationBeanScanner).isEqualTo(new ConfigurationBeanScanner(configurationClasses));
    }

    @DisplayName("`@Bean` 애너테이션이 적용된 메서드를 반환한다")
    @Test
    void scan_beans_annotation() {
        final BeanFactory beanFactory = new BeanFactory();
        final Map<Class<?>, Object> scan = configurationBeanScanner.scan2(beanFactory);
        beanFactory.addBean(scan);
        beanFactory.initialize();

        final DataSource dataSourceActual = beanFactory.getBean(DataSource.class);
        final MyJdbcTemplate myJdbcTemplateActual = beanFactory.getBean(MyJdbcTemplate.class);

        assertThat(dataSourceActual).isInstanceOf(DataSource.class);
        assertThat(myJdbcTemplateActual).isInstanceOf(MyJdbcTemplate.class);
    }
}
