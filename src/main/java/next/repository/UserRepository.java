package next.repository;

import next.model.User;

import java.util.List;

public interface UserRepository {

    void insert(User user);
    User findByUserId(String userId);
    List<User> findAll();
    void update(User user);
}
