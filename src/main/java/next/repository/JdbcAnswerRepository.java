package next.repository;

import core.annotation.Repository;
import core.jdbc.JdbcTemplate;
import core.jdbc.KeyHolder;
import core.jdbc.PreparedStatementCreator;
import core.jdbc.RowMapper;
import next.model.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class JdbcAnswerRepository implements JdbcRepository<Answer, Long> {

    private static final Logger logger = LoggerFactory.getLogger(JdbcAnswerRepository.class);
    private JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();

    @Override
    public Answer insert(Answer answer) {
        String sql = "INSERT INTO ANSWERS (writer, contents, createdDate, questionId) VALUES (?, ?, ?, ?)";
        PreparedStatementCreator psc = con -> {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, answer.getWriter());
            pstmt.setString(2, answer.getContents());
            pstmt.setTimestamp(3, new Timestamp(answer.getTimeFromCreateDate()));
            pstmt.setLong(4, answer.getQuestionId());
            return pstmt;
        };

        KeyHolder keyHolder = new KeyHolder();
        jdbcTemplate.update(psc, keyHolder);
        logger.debug("KeyHolder : {}", keyHolder);
        return findById(keyHolder.getId());
    }

    @Override
    public List<Answer> findAll() {
        return null;
    }

    @Override
    public Answer findById(Long answerId) {
        logger.debug("find AnswerId : {}", answerId);
        String sql = "SELECT answerId, writer, contents, createdDate, questionId FROM ANSWERS WHERE answerId = ?";

        RowMapper<Answer> rm = rs -> new Answer(rs.getLong("answerId"), rs.getString("writer"), rs.getString("contents"),
                rs.getTimestamp("createdDate"), rs.getLong("questionId"));

        return jdbcTemplate.queryForObject(sql, rm, answerId);
    }

    @Override
    public void update(Answer answer) {
        String sql = "UPDATE ANSWER set contents = ? WHERE answerId = ?";
        jdbcTemplate.update(sql, answer.getContents(), answer.getAnswerId());
    }

    @Override
    public void delete(Long answerId) {
        String sql = "DELETE FROM ANSWERS WHERE answerId = ?";
        jdbcTemplate.update(sql, answerId);
    }

    public List<Answer> findAllByQuestionId(long questionId) {
        String sql = "SELECT answerId, writer, contents, createdDate FROM ANSWERS WHERE questionId = ? "
                + "order by answerId desc";

        RowMapper<Answer> rm = rs -> new Answer(rs.getLong("answerId"), rs.getString("writer"), rs.getString("contents"),
                rs.getTimestamp("createdDate"), questionId);

        return jdbcTemplate.query(sql, rm, questionId);
    }
}
