package core.di.factory;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QuestionRepository;
import core.di.factory.example.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeanFactoryUtilsTest {

    @DisplayName("getInjectedClasses")
    @Nested
    class getInjectedClasses {

        @DisplayName("주입받는 클래스가 없으면 빈 배열을 리턴한다")
        @Test
        void whenNoDependentClasses() {
            // when
            Class<?>[] result = BeanFactoryUtils.getInjectedClasses(JdbcQuestionRepository.class);

            // then
            assertThat(result).isEmpty();
        }


        @DisplayName("주입받는 클래스가 있으면 해당 클래스가 담긴 배열을 리턴한다")
        @Test
        void whenHasDependentClasses() {
            // when
            Class<?>[] result = BeanFactoryUtils.getInjectedClasses(MyQnaService.class);

            // then
            assertThat(result).containsOnly(UserRepository.class, QuestionRepository.class);
        }
    }
}
