package next.dao;

import core.di.factory.BeanFactory;
import core.util.ReflectionUtils;
import next.dto.UserUpdatedDto;
import next.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import support.test.DBInitializer;

import java.util.List;
import java.util.Set;

import static core.mvc.DispatcherServlet.ALL_TARGET_TYPES;
import static org.assertj.core.api.Assertions.assertThat;

public class UserDaoTest {

    private UserDao userDao;

    private BeanFactory beanFactory;

    @BeforeEach
    public void setup() {
        DBInitializer.initialize();

        Reflections reflections = new Reflections("next.controller");
        Set<Class<?>> preInstanticateClazz = ReflectionUtils.getTypesAnnotatedWith(reflections, ALL_TARGET_TYPES);
        beanFactory = new BeanFactory(preInstanticateClazz);
        beanFactory.initialize();

        userDao = beanFactory.getBean(UserDao.class);
    }

    @Test
    public void crud() throws Exception {
        User expected = new User("userId", "password", "name", "javajigi@email.com");
        userDao.insert(expected);
        User actual = userDao.findByUserId(expected.getUserId());
        assertThat(actual).isEqualTo(expected);

        expected.update(new UserUpdatedDto("userId", "password2", "name2", "sanjigi@email.com"));
        userDao.update(expected);
        actual = userDao.findByUserId(expected.getUserId());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findAll() throws Exception {
        List<User> users = userDao.findAll();
        assertThat(users).hasSize(1);
    }
}
