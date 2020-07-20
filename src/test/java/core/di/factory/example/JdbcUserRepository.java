package core.di.factory.example;

import core.annotation.Inject;
import core.annotation.Repository;
@Repository
public class JdbcUserRepository implements UserRepository {
    private final MyJdbcTemplate jdbcTemplate;

    @Inject
    public JdbcUserRepository(MyJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MyJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
