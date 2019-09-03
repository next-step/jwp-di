package core.di.factory;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import next.MyConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    @Test
    void scan() {
        BeanFactory beanFactory = new BeanFactory();
        BeanScanner beanScanner = new BeanScanner("core.di.factory.example");
        beanScanner.scan(beanFactory);
        beanFactory.initialize();

        assertThat(beanFactory.getBean(MyQnaService.class)).isInstanceOf(MyQnaService.class);
    }
}