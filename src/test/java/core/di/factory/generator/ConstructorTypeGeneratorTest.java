package core.di.factory.generator;

import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanInitInfoExtractUtil;
import core.di.factory.BeanType;
import core.di.factory.circular.OneComponent;
import core.di.factory.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("일반 클래스의 빈을 생성하기 위한 제너레이터")
class ConstructorTypeGeneratorTest {
    private final ConstructorTypeGenerator generator = new ConstructorTypeGenerator();
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

    @ParameterizedTest
    @MethodSource
    @DisplayName("제너레이터가 특정 빈타입의 빈 생성을 지원하는지")
    void support(Class<?> clazz) {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(clazz);
        BeanInitInfo beanInitInfo = beanInitInfos.get(clazz);

        assertThat(generator.support(beanInitInfo)).isTrue();
    }

    private static Stream<Class<?>> support() {
        return Stream.of(QnaController.class, MyQnaService.class, JdbcUserRepository.class, OneComponent.class);
    }

    @Test
    @DisplayName("@Configuration 클래스는 지원하고, @Bean 은 지원 안하고")
    void supportConfigurationButBeanNot() {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(ExampleConfig.class);

        BeanInitInfo configurationClass = beanInitInfos.get(ExampleConfig.class);
        assertThat(generator.support(configurationClass)).isTrue();

        BeanInitInfo beanMethod = beanInitInfos.get(DataSource.class);
        assertThat(generator.support(beanMethod)).isFalse();
    }


    @Test
    @DisplayName("추가적인 빈이 필요없는 클래스 생성 테스트")
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
