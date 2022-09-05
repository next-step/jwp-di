package core.di.factory;

import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ClasspathBeanScannerTest {

    @DisplayName("scan메서드는 ComponentScan 애노테이션 기반으로 스캐닝하여 BeanRegister목록을 반환한다.")
    @Test
    void itIsReturnsBeanRegistersFromConfigurationClass() {
        ClasspathBeanScanner scanner = new ClasspathBeanScanner();

        Set<BeanRegister> registers = scanner.scan(IntegrationConfig.class);

        assertThat(registers).isNotEmpty()
                .contains(new ClassBeanRegister(JdbcQuestionRepository.class),
                        new ClassBeanRegister(JdbcUserRepository.class),
                        new ClassBeanRegister(MyQnaService.class),
                        new ClassBeanRegister(QnaController.class));
    }
}
