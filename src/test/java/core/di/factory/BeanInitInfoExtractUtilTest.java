package core.di.factory;

import core.di.factory.circular.OneComponent;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("클래스를 받아 Bean 초기화 정보를 생성해 주는 유틸 클래스")
class BeanInitInfoExtractUtilTest {

    @ParameterizedTest
    @MethodSource
    @DisplayName("Controller, Service, Repository, Component 의 경우")
    void extract(Class<?> clazz, BeanType beanType) {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(clazz);

        BeanInitInfo beanInitInfo = beanInitInfos.get(clazz);

        assertThat(beanInitInfos).hasSize(1);
        assertThat(beanInitInfo).isNotNull();
        assertThat(beanInitInfo).isEqualTo(new BeanInitInfo(clazz, beanType));
    }

    private static Stream<Arguments> extract() {
        return Stream.of(
                Arguments.of(QnaController.class, BeanType.CONTROLLER),
                Arguments.of(MyQnaService.class, BeanType.SERVICE),
                Arguments.of(JdbcUserRepository.class, BeanType.REPOSITORY),
                Arguments.of(OneComponent.class, BeanType.COMPONENT)
        );
    }

    @Test
    @DisplayName("어노테이션이 붙어있지 않은 클래스를 추출하려 할 경우")
    void extractNotAnnotatedClass() {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(BeanInitInfoTest.class);

        assertThat(beanInitInfos).isEmpty();
    }

    @Test
    @DisplayName("@Configuration 와 내부에 @Bean 어노테이션이 붙어있는 클래스의 경우")
    void extractConfigurationAndBean() throws NoSuchMethodException {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(ExampleConfig.class);

        assertThat(beanInitInfos).hasSize(2);

        BeanInitInfo configurationClass = beanInitInfos.get(ExampleConfig.class);
        assertThat(configurationClass).isNotNull();
        assertThat(configurationClass).isEqualTo(new BeanInitInfo(ExampleConfig.class, BeanType.CONFIGURATION));

        Method method = ExampleConfig.class
                .getDeclaredMethod("dataSource");
        BeanInitInfo beanClass = beanInitInfos.get(DataSource.class);
        assertThat(beanClass).isNotNull();
        assertThat(beanClass).isEqualTo(
                new BeanInitInfo(
                        DataSource.class,
                        new MethodInfo(ExampleConfig.class, method),
                        BeanType.BEAN
                )
        );
    }
}
