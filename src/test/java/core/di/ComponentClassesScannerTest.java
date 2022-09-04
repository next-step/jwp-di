package core.di;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComponentClassesScannerTest {

    @DisplayName("`@ComponentScan` 애너테이션이 적용된 패키지명을 찾는다")
    @Test
    void scan_with_component_scan_annotated_classes() {
        final String targetPackages = "core";

        final Object[] actual = ComponentClassesScanner.scanBasePackages(targetPackages);

        assertThat(actual).containsExactly("core");
    }

    @DisplayName("`@ComponentScan` 애너테이션이 적용된 클래스가 없으면 빈 목록을 반환한다")
    @Test
    void no_configuration_classes() {
        final String targetPackages = "mvc";

        final Object[] actual = ComponentClassesScanner.scanBasePackages(targetPackages);

        assertThat(actual).isEmpty();
    }

}
