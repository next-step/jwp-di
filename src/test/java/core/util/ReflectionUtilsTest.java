package core.util;

import static org.assertj.core.api.Assertions.assertThat;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

class ReflectionUtilsTest {

    @DisplayName("지정한 애너테이션들이 적용된 모든 클래스 목록을 반환한다")
    @Test
    void getTypesAnnotatedWith() {
        Reflections reflections = new Reflections("core.di.factory.example");

        final Set<Class<?>> actual = ReflectionUtils.getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);

        assertThat(actual).containsExactlyInAnyOrder(
            QnaController.class,
            MyQnaService.class,
            JdbcUserRepository.class,
            JdbcQuestionRepository.class
        );
    }
}
