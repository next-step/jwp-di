package core.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.common.collect.Maps;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QuestionRepository;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Created by iltaek on 2020/07/20 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
class BeanDefinitionsTest {

    @DisplayName("정적팩토리 메서드 newInstance 테스트")
    @Test
    void newInstanceTest() {
        BeanDefinitions newBd = BeanDefinitions.newInstance();

        assertNotNull(newBd);
        assertThat(newBd.getPreInstantiateBeans().size()).isZero();
        assertThat(newBd.getBeanDefinitionMap().size()).isZero();
    }

    @DisplayName("정적팩토리 메서드 from 테스트")
    @Test
    void fromTest() {
        BeanDefinitions oneBd = BeanDefinitions.from(new ClasspathBeanDefinition(JdbcQuestionRepository.class));

        assertNotNull(oneBd);
        assertThat(oneBd.getPreInstantiateBeans().size()).isEqualTo(1);
        assertThat(oneBd.getPreInstantiateBeans()).contains(JdbcQuestionRepository.class);
        assertThat(oneBd.getBeanDefinitionMap().size()).isEqualTo(1);

        assertThat(oneBd.getConcreteClass(QuestionRepository.class)).isEqualTo(JdbcQuestionRepository.class);
    }

    @DisplayName("정적팩토리 메서드 fromMap 테스트")
    @Test
    void fromMapTest() {
        Map<Class<?>, BeanDefinition> expected = Maps.newHashMap();
        expected.put(JdbcQuestionRepository.class, new ClasspathBeanDefinition(JdbcQuestionRepository.class));
        expected.put(MyQnaService.class, new ClasspathBeanDefinition(MyQnaService.class));
        BeanDefinitions mapBd = BeanDefinitions.fromMap(expected);

        assertNotNull(mapBd);
        assertThat(mapBd.getPreInstantiateBeans()).contains(JdbcQuestionRepository.class, MyQnaService.class);
        assertThat(mapBd.getBeanDefinitionMap().size()).isEqualTo(expected.size());
    }

}