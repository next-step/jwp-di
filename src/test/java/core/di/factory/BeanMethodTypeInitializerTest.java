package core.di.factory;

import core.annotation.Bean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class BeanMethodTypeInitializerTest {

    private BeanMethodTypeInitializer beanMethodTypeInitializer;

    @BeforeEach
    void setup() {
        beanMethodTypeInitializer = new BeanMethodTypeInitializer(new BeanFactory());
    }

    @DisplayName("type 이 method 이고 메소드에 @Bean 이 있으면 true")
    @Test
    void support() throws Exception {
        Method beanMethod = getClass().getDeclaredMethod("beanMethod");

        boolean support = beanMethodTypeInitializer.support(beanMethod);

        assertThat(support).isTrue();
    }

    @DisplayName("type 이 method 이지만 메소드에 @Bean 이 없으면 false")
    @Test
    void support_not_bean_annotation() throws Exception {
        Method notBeanMethod = getClass().getDeclaredMethod("notBeanMethod");

        boolean support = beanMethodTypeInitializer.support(notBeanMethod);

        assertThat(support).isFalse();
    }

    @DisplayName("type 이 method 가 아니면 false")
    @Test
    void support_class_type() throws Exception {
        Class<?> type = this.getClass();

        boolean support = beanMethodTypeInitializer.support(type);

        assertThat(support).isFalse();
    }

    @Bean
    void beanMethod() {

    }

    void notBeanMethod() {

    }
}