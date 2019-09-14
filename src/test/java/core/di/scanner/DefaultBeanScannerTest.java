package core.di.scanner;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.UserRepository;
import core.di.bean.BeanDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultBeanScannerTest {

    private static final String DI_DEFAULT_PACKAGE = "core.di.factory.example";
    private BeanScanner beanScanner;

    @BeforeEach
    void setUp() {
        beanScanner = new DefaultBeanScanner(DI_DEFAULT_PACKAGE);
    }

    @Test
    void enroll() {
        Set<BeanDefinition> beanDefinitions = beanScanner.scan();

        Set<? extends Class<?>> beanClass = beanDefinitions.stream()
                .map(BeanDefinition::getClazz)
                .collect(Collectors.toSet());

        assertThat(beanClass.contains(JdbcQuestionRepository.class)).isTrue();
        assertThat(beanClass.contains(MyQnaService.class)).isTrue();
        assertThat(beanClass.contains(UserRepository.class)).isFalse();
    }

}