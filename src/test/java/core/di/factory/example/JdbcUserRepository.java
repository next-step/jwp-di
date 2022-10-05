package core.di.factory.example;

import core.annotation.Inject;
import core.annotation.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcUserRepository implements UserRepository {

    private DataSource dataSource;

    public JdbcUserRepository() {
    }

    @Inject
    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Object getDataSource() {
        return dataSource;
    }
}
