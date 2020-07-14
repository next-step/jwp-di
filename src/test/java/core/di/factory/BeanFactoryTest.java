package core.di.factory;

import com.google.common.collect.Sets;
import core.di.BeanDefinition;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import core.di.factory.example.UserRepository;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.springframework.beans.BeanUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections("core.di.factory.example");

        Set<BeanDefinition> definitions = Sets.newHashSet(new BeanDefinition() {
            @Override
            public String getName() {
                return JdbcUserRepository.class.getSimpleName();
            }

            @Override
            public Method getMethod() {
                return null;
            }

            @Override
            public Constructor getConstructor() {
                try {
                    return JdbcUserRepository.class.getConstructor();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e.getCause());
                }
            }

            @Override
            public Class<?> getBeanClass() {
                return JdbcUserRepository.class;
            }
        });

        beanFactory = new BeanFactory();
        beanFactory.addBeanDefinitions(definitions);
        beanFactory.initialize();;
    }

    @Test
    public void di() throws Exception {
        UserRepository userRepository = beanFactory.getBean(UserRepository.class);
        JdbcUserRepository jdbcUserRepository = beanFactory.getBean(JdbcUserRepository.class);

        assertNotNull(userRepository);
        assertNotNull(jdbcUserRepository);
        assertEquals(userRepository, jdbcUserRepository);

    }

}
