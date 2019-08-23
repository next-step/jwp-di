package next.repository;

import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.jdbc.ConnectionManager;
import next.dto.UserUpdatedDto;
import next.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcUserRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(JdbcUserRepositoryTest.class);

    private BeanFactory beanFactory;
    private JdbcUserRepository repository;

    @BeforeEach
    public void setup() {
        beanFactory = new BeanFactory();
        BeanScanner beanScanner = new BeanScanner(beanFactory, "next.repository");
        beanScanner.scan();

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());

        repository = beanFactory.getBean(JdbcUserRepository.class);
    }

    @Test
    public void crud() throws Exception {
        User expected = new User("userId", "password", "name", "javajigi@email.com");
        repository.insert(expected);
        User actual = repository.findByUserId(expected.getUserId());
        assertThat(actual).isEqualTo(expected);

        expected.update(new UserUpdatedDto("password2", "name2", "sanjigi@email.com"));
        repository.update(expected);
        actual = repository.findByUserId(expected.getUserId());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findAll() throws Exception {
        List<User> users = repository.findAll();
        assertThat(users).hasSize(1);
    }
}