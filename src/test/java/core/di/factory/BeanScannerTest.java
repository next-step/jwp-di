package core.di.factory;

import core.di.factory.example.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeanScannerTest {

    @DisplayName("Controller 애노테이션의 스캔 여부")
    @Test
    @SuppressWarnings("unchecked")
    void beanScannerWithController() {
        final Set<BeanDefinition> beanDefinitions = BeanScanner.scan("core.di.factory.example");
        assertTrue(beanDefinitions.contains(new BeanDefinition(QnaController.class)));
    }

}
