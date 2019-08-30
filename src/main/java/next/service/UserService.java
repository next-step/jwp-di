package next.service;

import core.annotation.Inject;
import core.annotation.Service;
import next.dto.UserUpdatedDto;
import next.model.User;
import next.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Inject
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void insert(final User user) {
        userRepository.insert(user);
    }

    public User findByUserId(final String userId) {
        return userRepository.findByUserId(userId);
    }

    List<User> findAll() {
        return userRepository.findAll();
    }

    public void update(final String userId,
                       final UserUpdatedDto userUpdatedDto) {
        final User user = findByUserId(userId);
        user.update(userUpdatedDto);
        userRepository.update(user);
    }
}
