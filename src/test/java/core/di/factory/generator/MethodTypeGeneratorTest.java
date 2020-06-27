package core.di.factory.generator;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanInitInfoExtractUtil;
import core.di.factory.circular.OneComponent;
import core.di.factory.example.*;
import org.apache.commons.dbcp2.BasicDataSource;
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

@DisplayName("@Bean 과 같이 메소드로 정의되어있는 빈을 생성하기 위한 제너레이터")
class MethodTypeGeneratorTest {
    private final MethodTypeGenerator generator = new MethodTypeGenerator();
    private BeanFactory beanFactory;

    @BeforeEach
    private void setEnv() {
        beanFactory = new BeanFactory(new HashSet<>(
                Arrays.asList(ExampleConfig.class, IntegrationConfig.class))
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("제너레이터가 일반적인 컴포넌트 어노테이션 타입의 빈 생성을 지원하는지")
    void support(Class<?> clazz) {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(clazz);
        BeanInitInfo beanInitInfo = beanInitInfos.get(clazz);

        assertThat(generator.support(beanInitInfo)).isFalse();
    }

    private static Stream<Class<?>> support() {
        return Stream.of(QnaController.class, MyQnaService.class, JdbcUserRepository.class, OneComponent.class);
    }

    @Test
    @DisplayName("@Configuration 클래스는 지원 안 하고, @Bean 은 지원 하고")
    void supportBeanButConfigurationNot() {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(ExampleConfig.class);

        BeanInitInfo configurationClass = beanInitInfos.get(ExampleConfig.class);
        assertThat(generator.support(configurationClass)).isFalse();

        BeanInitInfo beanMethod = beanInitInfos.get(DataSource.class);
        assertThat(generator.support(beanMethod)).isTrue();
    }

    @Test
    @DisplayName("추가적인 빈이 필요없는 메서드 생성 테스트")
    void generate() {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(ExampleConfig.class);
        BeanInitInfo beanInitInfo = beanInitInfos.get(DataSource.class);

        Object bean = generator.generate(
                new LinkedHashSet<>(),
                beanFactory,
                beanInitInfo
        );

        assertThat(bean).isNotNull();
        assertThat(bean).isInstanceOf(DataSource.class);
    }

    @Test
    @DisplayName("빈 생성시 다른 빈이 필요한 경우")
    void generateChain() {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(BeanNeedClass.class);
        BeanInitInfo beanInitInfo = beanInitInfos.get(DataSource.class);

        Object bean = generator.generate(
                new LinkedHashSet<>(),
                beanFactory,
                beanInitInfo
        );

        assertThat(bean).isNotNull();
        assertThat(bean).isInstanceOf(DataSource.class);
    }

    // 빈 생성시 다른 @Bean 으로 생성된 빈이 필요한 경우는 종합 테스트에서!

    @Configuration
    public static class BeanNeedClass {
        @Bean
        public DataSource test(JdbcUserRepository jdbcUserRepository) {
            return new BasicDataSource();
        }
    }
}
