package next.dao;

import core.annotation.Inject;
import core.annotation.Repository;
import core.di.factory.example.MyJdbcTemplate;
import core.jdbc.RowMapper;
import next.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDao {

    private MyJdbcTemplate jdbcTemplate;

    private UserDao() { }

    @Inject
    public UserDao(MyJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(User user) {
        String sql = "INSERT INTO USERS VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUserId(), user.getPassword(), user.getName(), user.getEmail());
    }

    public User findByUserId(String userId) {
        String sql = "SELECT userId, password, name, email FROM USERS WHERE userid=?";

        RowMapper<User> rm = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs) throws SQLException {
                return new User(rs.getString("userId"), rs.getString("password"), rs.getString("name"),
                        rs.getString("email"));
            }
        };

        return jdbcTemplate.queryForObject(sql, rm, userId);
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT userId, password, name, email FROM USERS";

        RowMapper<User> rm = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs) throws SQLException {
                return new User(rs.getString("userId"), rs.getString("password"), rs.getString("name"),
                        rs.getString("email"));
            }
        };

        return jdbcTemplate.query(sql, rm);
    }

    public void update(User user) {
        String sql = "UPDATE USERS set password = ?, name = ?, email = ? WHERE userId = ?";
        jdbcTemplate.update(sql, user.getPassword(), user.getName(), user.getEmail(), user.getUserId());
    }

    public void delete(String userId) {
        String sql = "DELETE FROM USERS WHERE userId = ?";
        jdbcTemplate.update(sql, userId);
    }

    public void deleteAll() {
        String sql = "DELETE FROM USERS";
        jdbcTemplate.update(sql);
    }
}
