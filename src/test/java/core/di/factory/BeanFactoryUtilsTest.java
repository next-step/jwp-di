package core.di.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.QnaController;
import java.lang.reflect.Constructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

}
