package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.Inject;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections("core.di.factory.example");
        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory = new BeanFactory(preInstanticateClazz);
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

    @Test
    void name() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<Class<?>, Object> beans = Maps.newHashMap();

        for (final Constructor<?> declaredConstructor : MyQnaService.class.getDeclaredConstructors()) {
            final Object o = declaredConstructor.newInstance(new JdbcUserRepository(), new JdbcQuestionRepository());
            System.out.println(o);
        }
        beans.put(JdbcUserRepository.class, new JdbcUserRepository());
        beans.put(JdbcQuestionRepository.class, new JdbcQuestionRepository());


        for (final Constructor<?> constructor : MyQnaService.class.getConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                for (final Class<?> parameterType : constructor.getParameterTypes()) {
                    if (parameterType.isInterface()) {

                        final List<Class<?>> collect = beans.keySet().stream()
                                .filter(b -> Arrays.asList(b.getInterfaces()).contains(parameterType))
                                .collect(Collectors.toList());

                        for (final Class<?> aClass : collect) {

                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
