package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.example.QuestionRepository;
import core.di.factory.example.UserRepository;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeanFactoryTest {

    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    void setup() {
        reflections = new Reflections("core.di.factory.example");

        final Set<Class<?>> preInstantiateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory = new BeanFactory(preInstantiateClazz);
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

    @DisplayName("Bean 전체 설정이 된 것을 확인한다.")
    @Test
    void getBeans() {
        // given
        final Map<Class<?>, Object> beans = beanFactory.getBeans();
        final Class<?>[] keys = new Class[]{QnaController.class, MyQnaService.class,
                QuestionRepository.class, UserRepository.class};

        // then
        assertThat(beans).containsKeys(keys);
    }

    @DisplayName("Bean을 annotation으로 가져온다.")
    @ParameterizedTest
    @ValueSource(classes = {QnaController.class, MyQnaService.class, QuestionRepository.class, UserRepository.class})
    void getBeansOfAnnotatedBy(final Class<? extends Annotation> annotation) {
        // given
        final Map<Class<?>, Object> beans = beanFactory.getBeansOfAnnotatedBy(annotation);

        // then
        beans.keySet()
                .stream()
                .map(clazz -> clazz.isAnnotationPresent(annotation))
                .map(Assertions::assertThat)
                .forEach(AbstractBooleanAssert::isTrue);
    }

    @SafeVarargs
    private Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation>... annotations) {
        return Arrays.stream(annotations)
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
