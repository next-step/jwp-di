package core.di.factory;

import core.annotation.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeanDefinitionUtilTest {

    @DisplayName("빈의 정의를 잘 가지고 오는지 테스트해보자.")
    @Test
    void get_deps() {
        final BeanDefinition beanDefinition = BeanDefinitionUtil.buildBeanDefinition(DummyClass.class);
        final List<Class<?>> deps = beanDefinition.getDependencies();
        assertThat(beanDefinition).isNotNull();
        assertThat(deps).isNotNull();
        assertThat(deps.size()).isEqualTo(2);
    }

    public static class DummyClass {
        private DummyDeps1 dd1;
        private DummyDeps2 dd2;

        @Inject
        public DummyClass(DummyDeps1 dd1, DummyDeps2 dd2) {
            this.dd1 = dd1;
            this.dd2 = dd2;
        }
    }

    public static class DummyDeps1 {
        // no-op
    }

    public static class DummyDeps2 {
        // no-op
    }
}