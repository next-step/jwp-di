package core.di.factory.example;

import core.annotation.Inject;
import core.annotation.Repository;
import core.jdbc.JdbcTemplate;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Inject
    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
