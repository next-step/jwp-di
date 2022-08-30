package core.di.factory;

import core.di.factory.constructor.BeanConstructor;
import core.di.factory.constructor.ClassBeanConstructor;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.example.QuestionRepository;
import core.di.factory.example.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("클래스 경로 스캐너")
class ClasspathBeanScannerTest {

    @Test
    @DisplayName("설정된 경로의 Controller, Service, Repository 클래스 탐색")
    void scan() {
        //given
        ClasspathBeanScanner integrationConfigClassPathScanner = ClasspathBeanScanner.from(Collections.singleton(IntegrationConfig.class));
        //when
        Collection<BeanConstructor> constructors = integrationConfigClassPathScanner.scan();
        //then
        assertThat(constructors).containsExactlyInAnyOrder(
                ClassBeanConstructor.from(UserRepository.class),
                ClassBeanConstructor.from(QuestionRepository.class),
                ClassBeanConstructor.from(JdbcUserRepository.class),
                ClassBeanConstructor.from(JdbcQuestionRepository.class),
                ClassBeanConstructor.from(MyQnaService.class),
                ClassBeanConstructor.from(QnaController.class)
        );
    }
}
