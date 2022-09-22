package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.BeanDefinitionRegistry;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanFactoryTest {
    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections("core.di.factory.example");
        Set<Class<?>> preInstantiateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        BeanDefinitionRegistry beanDefinitionRegistry = new BeanDefinitionRegistry();
        beanDefinitionRegistry.registerClassPathBeans(preInstantiateClazz);
        beanDefinitionRegistry.registerConfigurationBeans(Set.of(TestConfig.class));
        beanFactory = new BeanFactory(beanDefinitionRegistry);
        beanFactory.initialize();
    }

    @Test
    @DisplayName("자동 스캔으로 주입된 빈의 의존관계 주입 테스트")
    void classPathBeanDI() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @Test
    @DisplayName("수동 스캔으로 주입된 빈의 의존관계 주입 테스트")
    void configurationBeanDI() throws Exception {
        TestA testA = beanFactory.getBean(TestA.class);

        assertNotNull(testA);
        assertNotNull(testA.getTestB());
        assertNotNull(testA.getTestD());

        TestB testB = testA.getTestB();
        assertNotNull(testB.getTestC());
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
