package core.di.factory;

import next.controller.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    @DisplayName("Bean 스캔이 정상적으로 되는지 확인한다.")
    @Test
    void scan() {
        BeanScanner beanScanner = new BeanScanner();
        Set<Class<?>> scannedBeans = beanScanner.scan("next");
        assertThat(scannedBeans).hasSize(5);
        assertThat(scannedBeans).contains(QnaController.class);
    }
}