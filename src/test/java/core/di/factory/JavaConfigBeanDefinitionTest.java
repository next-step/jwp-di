package core.di.factory;

import core.annotation.Bean;
import core.annotation.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@DisplayName("실험용 테스트..")
class JavaConfigBeanDefinitionTest {

    private static final Logger log = LoggerFactory.getLogger(JavaConfigBeanDefinitionTest.class);

    @DisplayName("JavaConfigBean")
    @Test
    void javaconfig() {
        final List<Method> beanMethods = new ArrayList<>();
        for (Method method : DummyConfig.class.getMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                log.debug("method: {}", method.getName());
                beanMethods.add(method);
            }
        }

    }

    @Configuration
    public static class DummyConfig {
        @Bean
        public DummyDep test() {
            return new DummyDep("test");
        }
    }

    public static class DummyDep {
        private final String data;

        public DummyDep(String data) {
            this.data = data;
        }
    }
}