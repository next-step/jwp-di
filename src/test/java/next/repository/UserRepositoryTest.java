package next.repository;

import next.dto.UserUpdatedDto;
import next.model.User;
import next.repository.impl.JdbcUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {

    private UserRepository userRepository = new JdbcUserRepository();

    @BeforeEach
    void setUp() {
        AutoConfigureTestDatabase.setup();
    }

    @Test
    void crud() {
        User expected = new User("userId", "password", "name", "javajigi@email.com");
        userRepository.insert(expected);
        User actual = userRepository.findByUserId(expected.getUserId());
        assertThat(actual).isEqualTo(expected);

        expected.update(new UserUpdatedDto("password2", "name2", "sanjigi@email.com"));
        userRepository.update(expected);
        actual = userRepository.findByUserId(expected.getUserId());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAll() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
    }
}