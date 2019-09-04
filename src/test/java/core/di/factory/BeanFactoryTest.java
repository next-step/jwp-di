package core.di.factory;

import core.di.ClasspathBeanScanner;
import core.di.BeanScanner;
import core.di.ConfigurationBeanScanner;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.assertj.core.util.Lists;
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
        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.registerConfiguration(ExampleConfig.class);
        scanners.add(configurationBeanScanner);
        scanners.add(new ClasspathBeanScanner(beanFactory, Collections.singleton("core.di.factory.example")));
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
