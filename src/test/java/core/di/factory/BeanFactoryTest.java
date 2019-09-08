package core.di.factory;

import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import next.configuration.MyTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactoryTest.class);

    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        ApplicationContext ctx = new ApplicationContext(MyTestConfiguration.class);
        beanFactory = ctx.initialize();
        beanFactory.initialize();
    }

    @Test
    public void di() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @Test
    @DisplayName("instantiate @Bean instant")
    void beanAnnotation() throws Exception {
        MyTestConfiguration configuration = new MyTestConfiguration();
        List<Method> methods = Arrays.asList(configuration.getClass().getDeclaredMethods());

        for (Method method : methods) {
            logger.debug("{}", method.getReturnType());
            assertThat(beanFactory.getBean(method.getReturnType())).isNotNull();
        }
    }
}
