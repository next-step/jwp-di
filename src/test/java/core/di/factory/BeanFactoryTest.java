package core.di.factory;

import core.di.factory.constructor.BeanConstructor;
import core.di.factory.container.BeanFactory;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.scanner.ClassPathBeanScanner;
import core.di.factory.scanner.ConfigurationBeanScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("빈 팩토리 - 빈의 생명주기, 어플리케이션 서비스 실행 관리")
public class BeanFactoryTest {

    private BeanFactory beanFactory;

    @BeforeEach
    public void setup() {
        ConfigurationBeanScanner configurationBeanScanner =
                new ConfigurationBeanScanner("core.di.factory.example");

        ClassPathBeanScanner classPathBeanScanner = new ClassPathBeanScanner(configurationBeanScanner.configurations());
        Collection<BeanConstructor> constructors = new ArrayList<>();
        constructors.addAll(configurationBeanScanner.scan());
        constructors.addAll(classPathBeanScanner.scan());
        beanFactory = new BeanFactory(constructors);
        beanFactory.initialize();
    }

    @DisplayName("의존관계 주입 빈 등록")
    @Test
    public void di() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }
}
