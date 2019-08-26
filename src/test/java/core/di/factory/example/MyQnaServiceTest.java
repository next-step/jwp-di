package core.di.factory.example;

import core.di.factory.BeanFactoryUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

class MyQnaServiceTest {

    @Test
    void reflectionCreate() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> clazz = Class.forName("core.di.factory.example.MyQnaService");
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);
        Object o1 = injectedConstructor.newInstance(new JdbcUserRepository(), new JdbcQuestionRepository());
        assertThat(o1).isInstanceOf(MyQnaService.class);
    }
}