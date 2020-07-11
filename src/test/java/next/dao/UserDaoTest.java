package next.dao;

import core.di.context.AnnotationConfigApplicationContext;
import core.di.context.ApplicationContext;
import core.jdbc.ConnectionManager;
import javax.sql.DataSource;
import next.config.NextConfiguration;
import next.dto.UserUpdatedDto;
import next.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import support.test.DBInitializer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    public void setup() {

        ApplicationContext ac = new AnnotationConfigApplicationContext(NextConfiguration.class);
        initDatabase(ac.getBean(DataSource.class));

        this.userDao = ac.getBean(UserDao.class);
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


    private void initDatabase(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
    }

}
