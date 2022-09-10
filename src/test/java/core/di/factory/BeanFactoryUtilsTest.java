package core.di.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import core.annotation.Repository;
import core.annotation.web.Controller;
import core.annotation.web.RequestParam;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.MockInjectedController;
import core.di.factory.example.QnaController;
import core.di.factory.example.QuestionRepository;
import java.lang.reflect.Constructor;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

class BeanFactoryUtilsTest {

    @DisplayName("`@Inject` 애너테이션이 적용된 생성자를 반환한다")
    @Test
    void injected_constructor() {
        final Class<?> qnaControllerClass = QnaController.class;

        final Constructor<?> actual = BeanFactoryUtils.getInjectedConstructor(qnaControllerClass);

        assertThat(actual).isNotNull();
        assertThat(actual.getParameterCount()).isEqualTo(1);
    }

    @DisplayName("`@Inject` 애너테이션이 적용된 생성자가 없으면 null 을 반환한다")
    @Test
    void has_no_injected_constructor() {
        final Class<?> jdbcQuestionRepositoryClass = JdbcQuestionRepository.class;

        final Constructor<?> actual = BeanFactoryUtils.getInjectedConstructor(jdbcQuestionRepositoryClass);

        assertThat(actual).isNull();
    }

    @DisplayName("@Inject 애너테이션이 적용된 생성자가 2개 이상이면 예외가 발생한다")
    @Test
    void exception_more_than_2_inject_constructors() {
        final Class<MockInjectedController> mockInjectedControllerClass = MockInjectedController.class;

        assertThatThrownBy(() -> BeanFactoryUtils.getInjectedConstructor(mockInjectedControllerClass))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("'@Inject' 애너테이션이 적용된 생성자는 반드시 1개만 존재해야 합니다. 생성자 수 : 2");
    }

    @DisplayName("인터페이스를 구현하지 않은 클래스를 찾아 반환한다")
    @Test
    void concrete_class() {
        Reflections reflections = new Reflections("core.di.factory.example");
        final Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(Controller.class);

        final Class<?> actual = BeanFactoryUtils.findConcreteClass(QnaController.class, controllerClasses);

        assertThat(actual).isEqualTo(QnaController.class);
    }

    @DisplayName("인터페이스를 구현한 구현체 클래스를 찾아 반환한다")
    @Test
    void implements_concrete_class() {
        Reflections reflections = new Reflections("core.di.factory.example");
        final Set<Class<?>> repositoryClasses = reflections.getTypesAnnotatedWith(Repository.class);

        final Class<?> actual = BeanFactoryUtils.findConcreteClass(QuestionRepository.class, repositoryClasses);

        assertThat(actual).isEqualTo(JdbcQuestionRepository.class);
    }

    @DisplayName("인터페이스를 구현한 구현체가 없으면 예외를 발생시킨다")
    @Test
    void concrete_class_is_not_exist() {
        Reflections reflections = new Reflections("core.di.factory.example");
        final Set<Class<?>> repositoryClasses = reflections.getTypesAnnotatedWith(Repository.class);

        final ThrowingCallable actual = () -> BeanFactoryUtils.findConcreteClass(RequestParam.class, repositoryClasses);

        assertThatThrownBy(actual)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageEndingWith(RequestParam.class.getName() + " 인터페이스를 구현하는 Bean이 존재하지 않는다.");
    }
}
