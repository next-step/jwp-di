package core.di.factory;

import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PreInstantiateBeansTest {

    @Test
    public void repositoryCreateBeanObject() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JdbcUserRepository.class);
        PreInstantiateBeans preInstantiateBeans = new PreInstantiateBeans(classes);

        Object object = preInstantiateBeans.createBeanObject(JdbcUserRepository.class);
        assertThat(object).isNotEqualTo(null);
    }

    @Test
    public void serviceCreateBeanObject() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JdbcUserRepository.class);
        classes.add(JdbcQuestionRepository.class);

        PreInstantiateBeans preInstantiateBeans = new PreInstantiateBeans(classes);

        Object object = preInstantiateBeans.createBeanObject(MyQnaService.class);
        assertThat(object).isNotEqualTo(null);
    }

    @Test
    public void controllerCreateBeanObject() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JdbcUserRepository.class);
        classes.add(JdbcQuestionRepository.class);
        classes.add(MyQnaService.class);

        PreInstantiateBeans preInstantiateBeans = new PreInstantiateBeans(classes);

        Object object = preInstantiateBeans.createBeanObject(QnaController.class);
        assertThat(object).isNotEqualTo(null);
    }

}
