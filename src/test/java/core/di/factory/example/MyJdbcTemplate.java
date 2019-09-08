package core.di.factory.example;

import core.annotation.Inject;

import javax.sql.DataSource;

public class MyJdbcTemplate {
    private DataSource dataSource;
    
    @Inject
    public MyJdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
