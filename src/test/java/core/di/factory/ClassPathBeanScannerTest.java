package core.di.factory;

import core.di.factory.constructor.BeanConstructor;
import core.di.factory.constructor.ClassBeanConstructor;
import core.di.factory.example.*;
import core.di.factory.scanner.ClassPathBeanScanner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@DisplayName("클래스 경로 스캐너")
public class ClassPathBeanScannerTest {

    @DisplayName("설정된 경로의 Controller, Service, Repository 클래스가 탐색되어야 한다.")
    @Test
    void scan() {
        ClassPathBeanScanner integrationConfigClassPathScanner =
                new ClassPathBeanScanner(Collections.singleton(IntegrationConfig.class));

        Collection<BeanConstructor> constructors = integrationConfigClassPathScanner.scan();

        assertThat(constructors).containsExactlyInAnyOrder(
                new ClassBeanConstructor(QnaController.class),
                new ClassBeanConstructor(MyQnaService.class),
                new ClassBeanConstructor(JdbcUserRepository.class),
                new ClassBeanConstructor(JdbcQuestionRepository.class),
                new ClassBeanConstructor(QuestionRepository.class),
                new ClassBeanConstructor(UserRepository.class)
        );
    }
}
