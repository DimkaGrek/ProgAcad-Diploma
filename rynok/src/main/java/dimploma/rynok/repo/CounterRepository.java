package dimploma.rynok.repo;

import dimploma.rynok.model.Counter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CounterRepository extends JpaRepository<Counter, Long> {

    @Query("SELECT u FROM Counter u WHERE u.parent = :parent")
    List<Counter> getCountersByParentId(@Param("parent") Long parentId);

    @Query("SELECT u FROM Counter u WHERE u.deleteDate IS NOT NULL")
    List<Counter> getDeletedCounters();

    @Query("SELECT COUNT(u) FROM Counter u WHERE u.deleteDate IS NOT NULL")
    Long getCountDeletedCounters();

    @Query("SELECT DISTINCT COUNT(e) > 0 FROM Counter e WHERE e.number = :field")
    boolean existsByNumber(@Param("field") Long field);
}
