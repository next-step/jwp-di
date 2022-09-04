package core.di.factory;

import static org.assertj.core.api.Assertions.assertThat;

import core.annotation.web.RequestMethod;
import core.di.factory.example.QnaController;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BeanScannerTest {

    @DisplayName("대상 패키지의 인스턴스를 BeanFactory 에 등록하고 HandlerExecution 을 찾을 수 있다")
    @Test
    void bean_scan() {
        final BeanFactory beanFactory = new BeanFactory();
        final BeanScanner beanScanner = new BeanScanner("core.di.factory.example");

        final Map<HandlerKey, HandlerExecution> actual = beanScanner.scan(beanFactory);

        final QnaController beanActual = beanFactory.getBean(QnaController.class);

        assertThat(beanActual).isNotNull();

        final HandlerKey expectedKey = new HandlerKey("/questions", RequestMethod.GET);
        final HandlerExecution handlerExecutionActual = actual.get(expectedKey);

        assertThat(actual).containsKey(expectedKey);
        assertThat(handlerExecutionActual).isNotNull().isInstanceOf(HandlerExecution.class);
    }
}
