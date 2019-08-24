package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanFactoryTest {

    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    void setup() {
        reflections = new Reflections("core.di.factory.example");

        final Set<Class<?>> pareInstantiateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory = new BeanFactory(pareInstantiateClazz);
        beanFactory.initialize();
    }

    @DisplayName("QnaController의 DI를 확인한다.")
    @Test
    void qnaControllerDi() {
        final QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        final MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @SafeVarargs
    private Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation>... annotations) {
        return Arrays.stream(annotations)
                .map(annotation -> reflections.getTypesAnnotatedWith(annotation))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
