package core.di;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentBasePackageScannerTest {

    @DisplayName("@ComponentScan의 basePackage 와 @ComponentScan이 달려 있는 모든 어노테이션의 package 를 스캔하기")
    @Test
    void scan() {
        /* given */
        ComponentBasePackageScanner basePackageScanner = new ComponentBasePackageScanner("core.di.factory.componentscan");

        /* when */
        Set<Object> basePackage = basePackageScanner.scan();

        /* then */
        assertThat(basePackage).containsExactlyInAnyOrder("core", "core.di.factory.componentscan");
    }

}
