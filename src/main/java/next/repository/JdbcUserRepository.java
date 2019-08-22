package next.repository;

import core.annotation.Repository;
import core.jdbc.JdbcTemplate;
import core.jdbc.RowMapper;
import next.model.User;

import java.util.List;

@Repository
public class JdbcUserRepository implements JdbcRepository<User, String> {
    private JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();

    @Override
    public User insert(User data) {
        String sql = "INSERT INTO USERS VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, data.getUserId(), data.getPassword(), data.getName(), data.getEmail());
        return data;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT userId, password, name, email FROM USERS";

        RowMapper<User> rm = rs -> new User(rs.getString("userId"), rs.getString("password"), rs.getString("name"),
                rs.getString("email"));
        return jdbcTemplate.query(sql, rm);
    }

    @Override
    public User findById(String userId) {
        String sql = "SELECT userId, password, name, email FROM USERS WHERE userid=?";

        RowMapper<User> rm = rs -> new User(rs.getString("userId"), rs.getString("password"), rs.getString("name"),
                rs.getString("email"));

        return jdbcTemplate.queryForObject(sql, rm, userId);
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE USERS set password = ?, name = ?, email = ? WHERE userId = ?";
        jdbcTemplate.update(sql, user.getPassword(), user.getName(), user.getEmail(), user.getUserId());
    }

    @Override
    public void delete(String userId) {
        String sql = "DELETE FROM USERS WHERE userId = ?";
        jdbcTemplate.update(sql, userId);
    }
}
