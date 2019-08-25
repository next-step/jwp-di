package next.dao;

import next.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {

    void insert(User user);

    User findByUserId(String userId);

    List<User> findAll() throws SQLException;

    void update(User user);
}
