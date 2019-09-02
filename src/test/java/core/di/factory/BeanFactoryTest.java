package core.di.factory;

import core.di.ComponentBeanScanner;
import core.di.BeanScanner;
import core.di.ConfigurationBeanScanner;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private BeanFactory beanFactory;

    @BeforeEach
    public void setup() {

        beanFactory = new BeanFactory();
        List<BeanScanner> scanners = Lists.newArrayList();
        scanners.add(new ConfigurationBeanScanner(beanFactory));
        scanners.add(new ComponentBeanScanner(beanFactory, Collections.singleton("core.di.factory.example")));
        scanners.forEach(BeanScanner::scan);
        beanFactory.initialize();
    }

    @Test
    public void di() {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }
}
