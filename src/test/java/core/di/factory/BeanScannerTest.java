package core.di.factory;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class BeanScannerTest {

    @Test
    void scanTest() {
        Set<Class<?>> preInstantiatedBeans = BeanScanner.getInstance().scan("core.di.factory");
        Set<Class<?>> expected = new HashSet<>(Arrays.asList(JdbcQuestionRepository.class, JdbcUserRepository.class, MyQnaService.class, QnaController.class));

        Assertions.assertThat(preInstantiatedBeans).isEqualTo(expected);
    }

    @Test
    void getHandlersTest() {
        Set<Object> expectedControllers = new HashSet<>(Arrays.asList(new QnaController(new MyQnaService(new JdbcUserRepository(), new JdbcQuestionRepository()))));
        Map<HandlerKey, HandlerExecution> handlers = BeanScanner.getInstance().getHandlers(expectedControllers);

        Assertions.assertThat(handlers.size()).isEqualTo(1);
    }
}
