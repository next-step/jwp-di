package core.di.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.di.BeanDefinitions;
import core.di.ClasspathBeanDefinition;
import core.di.factory.example.JdbcQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Created by iltaek on 2020/07/20 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class BeanFactoryTest {

    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        beanFactory = new BeanFactory();
    }

    @DisplayName("BeanRepository 인터페이스 구현 테스트")
    @Test
    void beanRepositoryTest() {
        Class<JdbcQuestionRepository> jdbcClazz = JdbcQuestionRepository.class;
        beanFactory.registerBeanDefinitions(BeanDefinitions.from(new ClasspathBeanDefinition(jdbcClazz)));
        beanFactory.initialize();

        JdbcQuestionRepository jdbcQuestionRepository = beanFactory.getBean(jdbcClazz);

        assertNotNull(jdbcQuestionRepository);

    }
}
