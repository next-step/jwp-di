package core.di.factory.example;

import core.annotation.Inject;
import core.annotation.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcUserRepository implements UserRepository {
    private DataSource dataSource;

    @Inject
    public JdbcUserRepository(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
