package next.repository;

import next.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User findByUserId(String userId);

    void insert(User user);

    void update(User user);
}