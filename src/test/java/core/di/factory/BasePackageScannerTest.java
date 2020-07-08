package core.di.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BasePackageScannerTest {
    private BasePackageScanner scanner;

    @BeforeEach
    void setUp() {
        scanner = new BasePackageScanner();
    }

    @Test
    void getBasePackage() {
        Object[] basePackage = scanner.getBasePackage();

        assertThat(basePackage.length).isEqualTo(2);
        assertThat(basePackage).contains("next", "core");
    }
}
