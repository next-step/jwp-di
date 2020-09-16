package core.di.factory.example;

import core.annotation.Inject;
import core.annotation.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcQuestionRepository implements QuestionRepository {

    private DataSource dataSource;

    @Inject
    public JdbcQuestionRepository(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
