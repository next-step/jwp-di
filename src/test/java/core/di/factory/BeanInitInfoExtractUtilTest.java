package core.di.factory;

import core.di.factory.circular.OneComponent;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
}
