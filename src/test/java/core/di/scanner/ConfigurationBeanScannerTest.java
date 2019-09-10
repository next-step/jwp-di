package core.di.scanner;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.scan.PackageTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationBeanScannerTest {

    private BeanFactory beanFactory;
    private ConfigurationBeanScanner cbs;

    @BeforeEach
    void setup() {
        beanFactory = new BeanFactory();
        cbs = new ConfigurationBeanScanner(beanFactory);
    }

    @Test
    public void register_simple() {
        cbs.register(ExampleConfig.class);
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }

    @Test
    public void register_classpathBeanScanner_통합() {
        cbs.register(IntegrationConfig.class);
        beanFactory.initialize();

        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanFactory);
        cbds.doScan("core.di.factory");

        assertNotNull(beanFactory.getBean(DataSource.class));

        JdbcUserRepository userRepository = beanFactory.getBean(JdbcUserRepository.class);
        assertNotNull(userRepository);
        assertNotNull(userRepository.getDataSource());

        MyJdbcTemplate jdbcTemplate = beanFactory.getBean(MyJdbcTemplate.class);
        assertNotNull(jdbcTemplate);
        assertNotNull(jdbcTemplate.getDataSource());
    }

    @DisplayName("@ComponentScan 에 패키지를 지정하지 않으면 설정파일이 위차한 패키지가 기본패키지가 된다.")
    @Test
    void getBasePackages_default_package() throws Exception {
        cbs.register(PackageTestConfig.class);

        String[] basePackages = cbs.getBasePackages();

        assertThat(basePackages[0]).isEqualTo("core.di.factory.example.scan");
    }

    @DisplayName("basePackages 의 값이 없으면 value 의 값을 basePackages 로 사용한다.")
    @Test
    void getBasePackages() {
        cbs.register(TestConfig.class);

        String[] basePackages = cbs.getBasePackages();

        assertThat(basePackages).hasSize(1);

        assertThat(basePackages[0]).isEqualTo("core.di.scanner");
    }

    @DisplayName("@ComponentScan 을 여러곳에 선언해두면 선언해둔 패키지 모두를 반환한다.")
    @Test
    void getBasePackages_many() throws Exception {
        Set<String> expected = ImmutableSet.of("core.di.factory.example.scan", "core.di.factory");

        cbs.register(IntegrationConfig.class);

        Set<String> basePackages = Sets.newHashSet(cbs.getBasePackages());

        assertThat(basePackages).isEqualTo(expected);
    }

    @DisplayName("@Configuration 이 없으면 에러")
    @Test
    void register() {
        assertThatThrownBy(() -> cbs.register(this.getClass()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Configuration 클래스만 가능합니다. class : [class core.di.scanner.ConfigurationBeanScannerTest]");
    }

    @Configuration
    @ComponentScan("core.di.scanner")
    private static class TestConfig {}
}