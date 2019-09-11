package core.di.bean;

import core.di.factory.example.*;
import core.di.bean.BeanDefinition;
import core.di.bean.DefaultBeanDefinition;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultBeanDefinitionTest {

    private Set<BeanDefinition> defaultBeanDefinitions = Sets.newHashSet();
    private DefaultBeanDefinition defaultBeanDefinition;

    @BeforeEach
    void setUp() {
        defaultBeanDefinitions.addAll(Arrays.asList(new DefaultBeanDefinition(JdbcQuestionRepository.class),
                new DefaultBeanDefinition(JdbcUserRepository.class),
                new DefaultBeanDefinition(MyQnaService.class),
                new DefaultBeanDefinition(QnaController.class)));
    }

    @Test
    void init() {
        defaultBeanDefinition = new DefaultBeanDefinition(MyQnaService.class);

        Class<?> clazz = defaultBeanDefinition.getClazz();

        assertThat(clazz).isEqualTo(MyQnaService.class);
    }

    @Test
    void init2() {
        defaultBeanDefinition = new DefaultBeanDefinition(JdbcUserRepository.class);

        Class<?> clazz = defaultBeanDefinition.getClazz();

        assertThat(clazz).isEqualTo(JdbcUserRepository.class);
    }
}