package core.di.factory.example;

import core.annotation.Component;
import core.annotation.Inject;

import javax.sql.DataSource;

@Component
public class MyJdbcTemplate {
    private final DataSource dataSource;

    @Inject
    public MyJdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
