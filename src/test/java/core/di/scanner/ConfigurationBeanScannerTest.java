package core.di.scanner;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationBeanScannerTest {

    private BeanFactory beanFactory;

    @BeforeEach
    void setup() {
        beanFactory = new BeanFactory();
    }

    @Test
    public void register_simple() {
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
        cbs.register(ExampleConfig.class);
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(DataSource.class));
    }

    @Test
    public void register_classpathBeanScanner_통합() {
        ConfigurationBeanScanner cbs = new ConfigurationBeanScanner(beanFactory);
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
        ConfigurationBeanScanner scanner = new ConfigurationBeanScanner(beanFactory);
        scanner.register(PackageTestConfig.class);

        String[] basePackages = scanner.getBasePackages();

        assertThat(basePackages[0]).isEqualTo("core.di.factory.example.scan");
    }

    @DisplayName("@ComponentScan 을 여러곳에 선언해두면 선언해둔 패키지 모두를 반환한다.")
    @Test
    void getBasePackages() throws Exception {
        Set<String> expected = ImmutableSet.of("core.di.factory.example.scan", "core.di.factory.example");

        ConfigurationBeanScanner scanner = new ConfigurationBeanScanner(beanFactory);
        scanner.register(IntegrationConfig.class);

        Set<String> basePackages = Sets.newHashSet(scanner.getBasePackages());

        assertThat(basePackages).isEqualTo(expected);
    }
}