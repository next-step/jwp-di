package core.di;

import core.di.factory.componentscan.ExampleConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentBasePackageScannerTest {

    @DisplayName("ComponentScan annotation 의 basePackage 등록하기")
    @Test
    void registerBasePackage() {
        /* given */
        ComponentBasePackageScanner basePackageScanner = new ComponentBasePackageScanner("core.di.factory.componentscan");

        /* when */
        basePackageScanner.registerBasePackage(ExampleConfiguration.class);

        /* then */
        assertThat(basePackageScanner.getBasePackages()).containsExactly("core");
    }

    @DisplayName("")
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
