package next.dao;

import static org.assertj.core.api.Assertions.assertThat;

import core.di.factory.ApplicationContext;
import java.util.List;
import javax.sql.DataSource;
import next.dto.UserUpdatedDto;
import next.model.User;
import next.support.config.MyWebAppConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.test.DBInitializer;

public class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    public void setup() {
        ApplicationContext ac = new ApplicationContext(MyWebAppConfiguration.class);
        DBInitializer.initialize(ac.getBean(DataSource.class));

        userDao = ac.getBean(UserDao.class);
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
