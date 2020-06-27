package core.di.factory;

import core.di.factory.example.ExampleConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("빈을 초기화 하기 위해 필요한 정보들을 담는 클래스")
class BeanInitInfoTest {

    @Test
    @DisplayName("equals 테스트")
    void equals() {
        assertThat(new BeanInitInfo(ExampleConfig.class, BeanType.CONFIGURATION))
                .isEqualTo(new BeanInitInfo(ExampleConfig.class, BeanType.CONFIGURATION));
    }

}