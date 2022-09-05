package core.util;

import static org.assertj.core.api.Assertions.assertThat;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import java.lang.reflect.Method;
import java.util.Set;
import javax.sql.DataSource;
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

    @DisplayName("메서드를 실행시킬 인스턴스와 메서드를 전달하여 메서드의 실행 결과를 반환한다")
    @Test
    void invoke_method() throws NoSuchMethodException {
        final Method dataSource = ExampleConfig.class.getMethod("dataSource");

        final Object actual = ReflectionUtils.invokeMethod(new ExampleConfig(), dataSource);

        assertThat(actual).isInstanceOf(DataSource.class);
    }

    @DisplayName("메서드를 실행시킬 인스턴스와 메서드, 인자를 전달하여 메서드의 실행 결과를 반환한다")
    @Test
    void invoke_method_with_arguments() throws NoSuchMethodException {
        final IntegrationConfig instance = new IntegrationConfig();
        final DataSource dataSource = instance.dataSource();
        final Method jdbcTemplate = IntegrationConfig.class.getMethod("jdbcTemplate", DataSource.class);

        final Object actual = ReflectionUtils.invokeMethod(instance, jdbcTemplate, dataSource);

        assertThat(actual).isInstanceOf(MyJdbcTemplate.class);
    }
}
