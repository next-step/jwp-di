package core.di.factory;

import core.di.factory.config.DefaultBeanDefinition;
import core.di.factory.support.DefaultListableBeanFactory;
import next.controller.ApiQnaController;
import next.service.QnaService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassPathBeanDefinitionScannerTest {

    @Test
    public void scan() throws Exception {
        DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

        scanner.scan("next", "core");

        assertThat(registry.getDefinitions())
                .contains(
                        new DefaultBeanDefinition(ApiQnaController.class)
                        , new DefaultBeanDefinition(QnaService.class)
                );
    }
}
