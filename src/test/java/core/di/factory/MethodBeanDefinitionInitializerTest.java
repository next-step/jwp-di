package core.di.factory;

import core.di.factory.example.ExampleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author KingCjy
 */
public class MethodBeanDefinitionInitializerTest {

    private DefaultBeanFactory beanFactory;
    private BeanInitializer beanInitializer;

    @BeforeEach
    public void setUp() throws NoSuchMethodException {
        beanInitializer = new MethodBeanDefinitionInitializer();
        beanFactory = new DefaultBeanFactory();
        beanFactory.registerDefinition(new ClassBeanDefinition(ExampleConfig.class, ExampleConfig.class.getName()));
    }

    @Test
    @DisplayName("@Bean 메서드 인스턴스 생성 테스트")
    public void initBeanTest() throws NoSuchMethodException {
        MethodBeanDefinition beanDefinition = new MethodBeanDefinition(ExampleConfig.class.getMethod("dataSource"));

        DataSource dataSource = (DataSource) beanInitializer.instantiate(beanDefinition, beanFactory);

        assertThat(beanInitializer.support(beanDefinition)).isTrue();
        assertThat(dataSource).isNotNull();
    }
}
