package core.di.factory.example;

import core.annotation.Inject;
import core.annotation.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final MyJdbcTemplate myJdbcTemplate;

    @Inject
    public JdbcUserRepository(MyJdbcTemplate myJdbcTemplate) {
        this.myJdbcTemplate = myJdbcTemplate;
    }

    public MyJdbcTemplate getMyJdbcTemplate() {
        return myJdbcTemplate;
    }

    public DataSource getDataSource() {
        return myJdbcTemplate.getDataSource();
    }
}
