package core.di.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.AnswerRepository;
import core.di.factory.example.JdbcAnswerRepository;
import core.di.factory.example.MyAnswerService;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

class BeanFactoryTest {

    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections("core.di.factory.example");
        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory = new BeanFactory(preInstanticateClazz);
        beanFactory.initialize();
    }

    @Test
    void di() {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    @DisplayName("Controller 애니테이션이 적용된 클래스만 반환한다")
    @Test
    void controller_types() {
        final Set<Class<?>> controllerTypes = beanFactory.getControllerTypes();

        assertThat(controllerTypes).containsExactly(QnaController.class);
    }

    @DisplayName("인스턴스 대상 클래스를 추가할 수 있다")
    @Test
    void add_pre_instantiate_classes() {
        beanFactory.addPreInstanticateBeans(MyAnswerService.class);
        beanFactory.addPreInstanticateBeans(JdbcAnswerRepository.class);
        beanFactory.initialize();

        final MyAnswerService bean = beanFactory.getBean(MyAnswerService.class);

        final AnswerRepository actual = bean.getAnswerRepository();

        assertThat(actual).isInstanceOf(JdbcAnswerRepository.class);
    }
}
