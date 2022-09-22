package next.dao;

import core.di.factory.ApplicationContext;
import core.jdbc.ConnectionManager;
import core.jdbc.JdbcTemplate;
import next.config.MyConfiguration;
import next.dto.UserUpdatedDto;
import next.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.test.DBInitializer;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    public void setup() {
        ApplicationContext applicationContext = new ApplicationContext(MyConfiguration.class);
        applicationContext.initialize();
        DBInitializer.initialize(applicationContext.getBean(DataSource.class));

        userDao = new UserDao(new JdbcTemplate(ConnectionManager.getDataSource()));
    }

    @Test
    void crud() throws Exception {
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
    void findAll() throws Exception {
        List<User> users = userDao.findAll();
        assertThat(users).hasSize(1);
    }
}
