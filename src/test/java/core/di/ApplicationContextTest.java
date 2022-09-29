package core.di;

import core.configuration.TestApplicationConfiguration;
import core.di.factory.BeanFactory;
import core.di.factory.example.QnaController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

class ApplicationContextTest {

    @Test
    void initializeTest() {
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.initialize(TestApplicationConfiguration.class);

        BeanFactory beanFactory = applicationContext.getBeanFactory();
        Assertions.assertThat(beanFactory.getBean(QnaController.class)).isNotNull();
    }
}
