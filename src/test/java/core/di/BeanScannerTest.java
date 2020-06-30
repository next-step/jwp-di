package core.di;

import core.annotation.web.RequestMethod;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    private static final String basePackage = "core.di.factory.example";
    private static BeanScanner beanScanner;

    @BeforeAll
    static void setUp() {
        beanScanner = new BeanScanner();
    }

    @Test
    @DisplayName("핸들러를 정상적으로 가져올 수 있다")
    void beanTest() {
        final HandlerKey expectedKey = new HandlerKey("/questions", RequestMethod.GET);

        final Map<HandlerKey, HandlerExecution> result = beanScanner.scan(basePackage);

        assertThat(result).isNotEmpty();
        assertThat(result.get(expectedKey)).isNotNull();
    }
}