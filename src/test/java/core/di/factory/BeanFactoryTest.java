package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections("core.di.factory.example");
        Set<Class<?>> preInstantiateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory = new BeanFactory(preInstantiateClazz);
        beanFactory.initialize();
    }

    @Test
    public void di() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    @Test
    public void repositoryCreateBeanObject() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JdbcUserRepository.class);
        BeanFactory preInstantiateBeans = new BeanFactory(classes);

        Object object = preInstantiateBeans.instantiateClass(JdbcUserRepository.class);
        assertThat(object).isNotEqualTo(null);
    }

    @Test
    public void serviceCreateBeanObject() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JdbcUserRepository.class);
        classes.add(JdbcQuestionRepository.class);

        BeanFactory preInstantiateBeans = new BeanFactory(classes);

        Object object = preInstantiateBeans.instantiateClass(MyQnaService.class);
        assertThat(object).isNotEqualTo(null);
    }

    @Test
    public void controllerCreateBeanObject() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JdbcUserRepository.class);
        classes.add(JdbcQuestionRepository.class);
        classes.add(MyQnaService.class);

        BeanFactory preInstantiateBeans = new BeanFactory(classes);

        Object object = preInstantiateBeans.instantiateClass(QnaController.class);
        assertThat(object).isNotEqualTo(null);
    }
}
