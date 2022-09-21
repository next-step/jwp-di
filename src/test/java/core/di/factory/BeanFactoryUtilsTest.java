package core.di.factory;

import core.annotation.Inject;
import core.annotation.Service;
import core.di.factory.example.QuestionRepository;
import core.di.factory.example.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class BeanFactoryUtilsTest {
    @Test
    @DisplayName("getInjectedConstructor 인자로 전달되는 클래스의 생성자 중 @Inject 애노테이션 설정이 2개 이상일 경우 예외가 발생한다.")
    void throwExceptionWhenInjectMoreThanOne() {
        assertThatThrownBy(() -> BeanFactoryUtils.getInjectedConstructor(MyTestQnaService.class)).isInstanceOf(IllegalArgumentException.class);
    }

    final class MyTestQnaService {
        private UserRepository userRepository;
        private QuestionRepository questionRepository;

        @Inject
        public MyTestQnaService(UserRepository userRepository, QuestionRepository questionRepository) {
            this.userRepository = userRepository;
            this.questionRepository = questionRepository;
        }

        @Inject
        public MyTestQnaService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }
    }
}