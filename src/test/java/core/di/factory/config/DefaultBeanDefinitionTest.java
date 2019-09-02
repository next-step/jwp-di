package core.di.factory.config;

import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

class DefaultBeanDefinitionTest {

    @Test
    void hasInjectConstructorClass() {
        Class<?> clazz = QnaController.class;
        DefaultBeanDefinition defaultBeanDefinition = new DefaultBeanDefinition(clazz);
        assertThat(defaultBeanDefinition.getInjectConstructor().get()).isEqualTo(clazz.getDeclaredConstructors()[0]);
        assertThat(defaultBeanDefinition.getBeanClass()).isEqualTo(clazz);

    }

    @Test
    void hasNotInjectConstructorClass() {
        Class<?> clazz = JdbcUserRepository.class;
        DefaultBeanDefinition defaultBeanDefinition = new DefaultBeanDefinition(clazz);
        assertThat(defaultBeanDefinition.getInjectConstructor().isPresent()).isEqualTo(false);
        assertThat(defaultBeanDefinition.getBeanClass()).isEqualTo(clazz);
    }
}