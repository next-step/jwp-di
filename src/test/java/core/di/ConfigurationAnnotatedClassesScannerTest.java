package core.di;

import static org.assertj.core.api.Assertions.assertThat;

import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfigurationAnnotatedClassesScannerTest {

    @DisplayName("`@Configuration` 애너테이션이 적용된 클래스들을 찾는다")
    @Test
    void scan_configuration_classes() {
        //given
        final String targetPackages = "core.di.factory.example";

        //when
        Set<Class<?>> actual = ConfigurationAnnotatedClassesScanner.scan(targetPackages);

        //then
        assertThat(actual).containsExactlyInAnyOrder(ExampleConfig.class, IntegrationConfig.class);
    }

    @DisplayName("`@Configuration` 애너테이션이 적용된 클래스가 없으면 빈 목록을 반환한다")
    @Test
    void no_configuration_classes() {
        //given
        final String targetPackages = "core.mvc";

        //when
        Set<Class<?>> actual = ConfigurationAnnotatedClassesScanner.scan(targetPackages);

        //then
        assertThat(actual).isEmpty();
    }
}
