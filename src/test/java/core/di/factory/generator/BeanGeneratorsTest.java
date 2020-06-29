package core.di.factory.generator;

import core.di.exception.BeanCreateException;
import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanInitInfoExtractUtil;
import core.di.factory.example.ExampleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("빈 제너레이터 종합")
class BeanGeneratorsTest {
    private final BeanGenerators beanGenerators = new BeanGenerators(
            Arrays.asList(new ConstructorTypeGenerator(), new MethodTypeGenerator())
    );
    private BeanFactory beanFactory;

    @BeforeEach
    private void setEnv() {
        beanFactory = new BeanFactory(new HashSet<>(
                Collections.singletonList(ExampleConfig.class))
        );
    }

    @Test
    @DisplayName("여러 타입의 빈을 생성함에 있어서 문제가 없는지")
    void generate() {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(ExampleConfig.class);

        beanInitInfos.values()
                .stream()
                .map(beanInitInfo -> beanGenerators.generate(new LinkedHashSet<>(), beanFactory, beanInitInfo))
                .forEach(bean -> assertThat(bean).isNotNull());
    }

    @Test
    @DisplayName("지원하는 빈 제너레이터가 없다면 예외를 발생한다")
    void generateFail() {
        Map<Class<?>, BeanInitInfo> beanInitInfos = BeanInitInfoExtractUtil.extractBeanInitInfo(ExampleConfig.class);
        BeanGenerators beanGenerators = new BeanGenerators(Collections.singleton(new ConstructorTypeGenerator()));

        assertThatExceptionOfType(BeanCreateException.class)
                .isThrownBy(() ->
                        beanInitInfos.values()
                                .forEach(beanInitInfo ->
                                        beanGenerators.generate(
                                                new LinkedHashSet<>(),
                                                beanFactory,
                                                beanInitInfo
                                        )
                                )
                );
    }
}
