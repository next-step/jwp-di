package next.repository;

import java.util.List;

public interface JdbcRepository<T, ID> {
    T insert(T data);
    List<T> findAll();
    T findById(ID id);
    void update(T data);
    void delete(ID id);
}
