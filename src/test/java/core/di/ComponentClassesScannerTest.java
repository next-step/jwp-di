package core.di;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComponentClassesScannerTest {

    @DisplayName("`@ComponentScan` 애너테이션이 적용된 패키지명을 찾는다")
    @Test
    void scan_with_component_scan_annotated_classes() {
        final String targetPackages = "core";

        final Set<String> actual = ComponentClassesScanner.scanBasePackages(targetPackages);

        assertThat(actual).containsExactlyInAnyOrder("core", "next");
    }

    @DisplayName("`@ComponentScan` 애너테이션이 적용된 클래스가 없으면 빈 목록을 반환한다")
    @Test
    void no_configuration_classes() {
        final String targetPackages = "next";

        final Set<String> actual = ComponentClassesScanner.scanBasePackages(targetPackages);

        assertThat(actual).isEmpty();
    }

}
