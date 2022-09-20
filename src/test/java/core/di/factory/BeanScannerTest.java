package core.di.factory;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class BeanScannerTest {

    @Test
    void scanTest() {
        Set<Class<?>> preInstantiatedBeans = BeanScanner.getInstance().scan("core.di.factory");
        Set<Class<?>> expected = new HashSet<>(Arrays.asList(JdbcQuestionRepository.class, JdbcUserRepository.class, MyQnaService.class, QnaController.class));

        Assertions.assertThat(preInstantiatedBeans).isEqualTo(expected);
    }

    @Test
    void getHandlersTest() {
        QnaController qnaController = new QnaController(new MyQnaService(new JdbcUserRepository(), new JdbcQuestionRepository()));
        Map<Class<?>, Object> beans = new HashMap<>() {{
            put(QnaController.class, qnaController);
        }};
        Map<HandlerKey, HandlerExecution> handlers = BeanScanner.getInstance().getHandlers(beans);

        Assertions.assertThat(handlers.size()).isEqualTo(1);
    }
}
