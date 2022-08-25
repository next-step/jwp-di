package core.di.factory;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.example.QuestionRepository;
import core.di.factory.example.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("빈 스캐너")
class BeanScannerTest {

    private static final String BASE_PACKAGE = "core.di.factory.example";

    @Test
    @DisplayName("생성")
    void instance() {
        assertThatNoException().isThrownBy(() -> BeanScanner.packages(BASE_PACKAGE));
    }

    @Test
    @DisplayName("기본 패키지는 필수")
    void instance_null_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException().isThrownBy(() -> BeanScanner.packages(null));
    }

    @Test
    @DisplayName("Controller, Service, Repository 클래스 탐색")
    void scan() {
        assertThat(BeanScanner.packages(BASE_PACKAGE).scan())
                .containsExactlyInAnyOrder(UserRepository.class,
                        QuestionRepository.class,
                        JdbcUserRepository.class,
                        JdbcQuestionRepository.class,
                        MyQnaService.class,
                        QnaController.class);
    }

    @Test
    @DisplayName("UserRepository 의 서브 타입 탐색하면 JdbcUserRepository")
    void subTypeOf() {
        assertThat(BeanScanner.packages(BASE_PACKAGE).subTypeOf(UserRepository.class))
                .isEqualTo(JdbcUserRepository.class);
    }
}
