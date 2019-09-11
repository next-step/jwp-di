package core.di.tobe.scanner;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.UserRepository;
import core.di.tobe.bean.BeanDefinition;
import core.di.tobe.BeanFactory;
import core.di.tobe.DefaultBeanFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultBeanScannerTest {

    private static final String DI_DEFAULT_PACKAGE = "core.di.factory.example";
    private BeanFactory beanFactory = DefaultBeanFactory.getInstance();
    private BeanScanner beanScanner;

    @BeforeEach
    void setUp() {
        beanScanner = new DefaultBeanScanner(beanFactory, DI_DEFAULT_PACKAGE);
    }

    @Test
    void enroll() {
        Set<BeanDefinition> enroll = beanScanner.enroll();
        Set<? extends Class<?>> collect = enroll.stream().map(BeanDefinition::getClazz).collect(Collectors.toSet());

        assertThat(collect.contains(JdbcQuestionRepository.class)).isTrue();
        assertThat(collect.contains(MyQnaService.class)).isTrue();
        assertThat(collect.contains(UserRepository.class)).isFalse();
    }

}