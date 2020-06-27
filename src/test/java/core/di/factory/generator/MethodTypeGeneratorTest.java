package core.di.factory.generator;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanInitInfoExtractUtil;
import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.JdbcUserRepository;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

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
