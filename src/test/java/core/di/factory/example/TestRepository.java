package core.di.factory.example;

import core.annotation.Inject;
import core.annotation.Repository;

@Repository
public class TestRepository {

    private final MyJdbcTemplate jdbcTemplate;

    @Inject
    public TestRepository(MyJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MyJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
