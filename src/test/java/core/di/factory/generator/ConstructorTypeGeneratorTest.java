package core.di.factory.generator;

import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanType;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("일반 클래스의 빈을 생성하기 위한 제너레이터")
class ConstructorTypeGeneratorTest {
    private ConstructorTypeGenerator generator = new ConstructorTypeGenerator();
    private BeanFactory beanFactory;

    @BeforeEach
    private void setEnv() {
        beanFactory = new BeanFactory(
                new HashSet<>(
                        Arrays.asList(
                                QnaController.class,
                                MyQnaService.class,
                                JdbcUserRepository.class,
                                JdbcQuestionRepository.class
                        )
                )
        );
    }

    @Test
    @DisplayName("일반 클래스 생성 테스트")
    void generate() {
        Object bean = generator.generate(
                new LinkedHashSet<>(),
                beanFactory,
                new BeanInitInfo(JdbcUserRepository.class, BeanType.REPOSITORY)
        );

        assertThat(bean).isNotNull();
        assertThat(bean.getClass()).isEqualTo(JdbcUserRepository.class);
    }

    @Test
    @DisplayName("클래스 생성시 다른 빈이 필요한 경우")
    void generateChain() {
        QnaController bean = (QnaController) generator.generate(
                new LinkedHashSet<>(),
                beanFactory,
                new BeanInitInfo(QnaController.class, BeanType.REPOSITORY)
        );

        assertThat(bean).isNotNull();
        assertThat(bean.getClass()).isEqualTo(QnaController.class);

        MyQnaService service = bean.getQnaService();
        assertThat(service).isNotNull();

        assertThat(service.getQuestionRepository()).isNotNull();
        assertThat(service.getUserRepository()).isNotNull();
    }
}
