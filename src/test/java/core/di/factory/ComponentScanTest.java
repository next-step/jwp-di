package core.di.factory;

import core.annotation.ComponentScan;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentScanTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationTest.class);

    @Test
    public void componentScan() {
        BeanScanner beanScanner = new BeanScanner();
        Set<String> basePackages = new HashSet<>();
        Set<Class<?>> classes = beanScanner.scan(ComponentScan.class);
        for (Class<?> clazz : classes) {
            ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            basePackages.addAll(Arrays.asList(componentScan.value()));
        }

        BeanFactory beanFactory = new BeanFactory(beanScanner.scan(basePackages));
        beanFactory.initialize();
        Object obj = beanFactory.getBean(QnaController.class);
        assertThat(obj).isNotEqualTo(null);
    }
}
