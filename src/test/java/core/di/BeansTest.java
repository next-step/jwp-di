package core.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import core.di.factory.example.JdbcQuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Created by iltaek on 2020/07/20 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
class BeansTest {

    @DisplayName("Beans의 Bean 생성 테스트")
    @Test
    void instantiateTest() {
        Beans beans = new Beans();
        beans.instantiateBeans(BeanDefinitions.from(new ClasspathBeanDefinition(JdbcQuestionRepository.class)));

        Object jdbcQuestionRepository = beans.get(JdbcQuestionRepository.class);

        assertNotNull(jdbcQuestionRepository);
        assertThat(jdbcQuestionRepository.getClass()).isEqualTo(JdbcQuestionRepository.class);
    }
}