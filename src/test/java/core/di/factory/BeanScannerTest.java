package core.di.factory;

import static org.assertj.core.api.Assertions.assertThat;

import core.annotation.web.RequestMethod;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BeanScannerTest {

    @DisplayName("HandlerExcution 을 찾을 수 있다")
    @Test
    void bean_scan() {
        final BeanScanner beanScanner = new BeanScanner();
        final Map<HandlerKey, HandlerExecution> scan = beanScanner.scan("core.di.factory.example");

        final HandlerKey expectedKey = new HandlerKey("/questions", RequestMethod.GET);

        assertThat(scan).containsKey(expectedKey);
        assertThat(scan.get(expectedKey)).isNotNull().isInstanceOf(HandlerExecution.class);
    }

}
