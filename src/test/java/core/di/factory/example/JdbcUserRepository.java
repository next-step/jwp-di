package core.di.factory.example;

import javax.sql.DataSource;

import core.annotation.Inject;
import core.annotation.Repository;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final MyJdbcTemplate myJdbcTemplate;

    @Inject
    public JdbcUserRepository(MyJdbcTemplate myJdbcTemplate) {
        this.myJdbcTemplate = myJdbcTemplate;
    }

    public DataSource getDataSource() {
        return myJdbcTemplate.getDataSource();
    }
}
