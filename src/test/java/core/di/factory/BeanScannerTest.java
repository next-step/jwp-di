package core.di.factory;

import core.annotation.web.Controller;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeanScannerTest {

    private BeanScanner beanScanner;

    @BeforeEach
    void setUp() {
        beanScanner = new BeanScanner("core.di.factory.example");
    }

    @DisplayName("Controller 애노테이션의 스캔 여부")
    @Test
    @SuppressWarnings("unchecked")
    void beanScannerWithController() {
        final BeanDefinitions beanDefinitions = beanScanner.scan(Controller.class);
        assertTrue(beanDefinitions.contains(QnaController.class));
    }

}
