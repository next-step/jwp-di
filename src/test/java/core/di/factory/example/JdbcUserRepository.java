package core.di.factory.example;

import core.annotation.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcUserRepository implements UserRepository {

    private DataSource dataSource;

    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
