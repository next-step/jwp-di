package core.mvc.tobe;

import core.di.factory.example.QnaController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    @Test
    void ComponentScan으로_basePackage_설정() {
        BeanScanner beanScanner = new BeanScanner();

        assertThat(beanScanner.getTypesAnnotatedWith()).contains(QnaController.class);
    }
}