package core.di.factory;

import core.di.ComponentBeanScanner;
import core.di.factory.example.MyQnaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author : yusik
 * @date : 01/09/2019
 */
public class ComponentBeanScannerTest {

    @DisplayName("컴포넌트 기본 scan 테스트")
    @Test
    public void defaultScan() {

        BeanFactory beanFactory = new BeanFactory();
        ComponentBeanScanner scanner = new ComponentBeanScanner(beanFactory, Collections.singleton("core.di.factory.example"));
        scanner.scan();
        beanFactory.initialize();

        assertNotNull(beanFactory.getBean(MyQnaService.class));
    }
}
