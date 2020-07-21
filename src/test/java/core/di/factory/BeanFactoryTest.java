package core.di.factory;

import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private BeanFactory beanFactory;

    @BeforeEach
    public void setup() {
        BeanDefinitions beanDefinitions = new BeanDefinitions();
        ClasspathBeanScanner cbds = new ClasspathBeanScanner(beanDefinitions);
        cbds.doScan(BeanFactoryTestConfig.class);
        beanFactory = new BeanFactory(beanDefinitions);
        beanFactory.initialize();
    }

    @DisplayName("BeanFactory가 빈을 정상적으로 생성해주는지 확인한다")
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
