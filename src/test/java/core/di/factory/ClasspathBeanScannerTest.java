package core.di.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Created by iltaek on 2020/07/20 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class ClasspathBeanScannerTest {

    @DisplayName("Classpath에 Annotation으로 설정된 Bean 스캔 테스트")
    @Test
    void registerBeansWithConfigClassTest() {
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanDefinitionScanner configBds = new ConfigurationBeanDefinitionScanner(beanFactory);
        configBds.register(IntegrationConfig.class);

        ClasspathBeanDefinitionScanner classpathBds = new ClasspathBeanDefinitionScanner(beanFactory);
        classpathBds.setAnnotations(Controller.class, Service.class, Repository.class);
        classpathBds.doScan("core.di.factory.example");

        beanFactory.initialize();

        QnaController qnaController = beanFactory.getBean(QnaController.class);
        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());
    }

}
