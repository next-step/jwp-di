package core.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTemplate1 {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplate1.class);

    private final DataSource dataSource;

    public JdbcTemplate1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(String sql, Object... parameters) {
        update(sql, createPreparedStatementSetter(parameters));
    }

    public void update(String sql, PreparedStatementSetter pss) throws DataAccessException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pss.setParameters(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private PreparedStatementSetter createPreparedStatementSetter(Object... parameters) {
        return pstmt -> {
            for (int i = 0; i < parameters.length; i++) {
                pstmt.setObject(i + 1, parameters[i]);
            }
        };
    }

    public void update(PreparedStatementCreator psc, KeyHolder holder) {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = psc.createPreparedStatement(conn)) {
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                long generatedKey = rs.getLong(1);
                logger.debug("Generated Key : {}", generatedKey);
                holder.setId(generatedKey);
            }
            rs.close();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public <T> T queryForObject(String sql, RowMapper<T> rm, Object... parameters) {
        return queryForObject(sql, rm, createPreparedStatementSetter(parameters));
    }

    public <T> T queryForObject(String sql, RowMapper<T> rm, PreparedStatementSetter pss) {
        List<T> list = query(sql, rm, pss);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public <T> List<T> query(String sql, RowMapper<T> rm, PreparedStatementSetter pss) throws DataAccessException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pss.setParameters(pstmt);
            return mapResultSetToObject(rm, pstmt);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private <T> List<T> mapResultSetToObject(RowMapper<T> rm, PreparedStatement pstmt) {
        try (ResultSet rs = pstmt.executeQuery()) {
            List<T> list = new ArrayList<T>();
            while (rs.next()) {
                list.add(rm.mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> rm, Object... parameters) {
        return query(sql, rm, createPreparedStatementSetter(parameters));
    }
}
