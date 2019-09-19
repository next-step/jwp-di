package next.repository.impl;


import core.annotation.Inject;
import core.annotation.Repository;
import core.jdbc.JdbcTemplate;
import core.jdbc.RowMapper;
import next.model.User;
import next.repository.UserRepository;

import java.util.List;

@Repository
public class JdbcUserRepository implements UserRepository {

    private JdbcTemplate jdbcTemplate;

    @Inject
    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(User user) {
        String sql = "INSERT INTO USERS VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUserId(), user.getPassword(), user.getName(), user.getEmail());
    }

    @Override
    public User findByUserId(String userId) {
        String sql = "SELECT userId, password, name, email FROM USERS WHERE userid=?";

        RowMapper<User> rm = rs -> new User(
                rs.getString("userId"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("email"));

        return jdbcTemplate.queryForObject(sql, rm, userId);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT userId, password, name, email FROM USERS";

        RowMapper<User> rm = rs -> new User(
                rs.getString("userId"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("email"));

        return jdbcTemplate.query(sql, rm);
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE USERS set password = ?, name = ?, email = ? WHERE userId = ?";
        jdbcTemplate.update(sql, user.getPassword(), user.getName(), user.getEmail(), user.getUserId());
    }
}