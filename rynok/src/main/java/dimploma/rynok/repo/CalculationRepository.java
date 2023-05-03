package dimploma.rynok.repo;

import dimploma.rynok.model.Calculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalculationRepository extends JpaRepository<Calculation, Long> {
    @Query(value = "SELECT calc.* FROM calculation as calc, counters as c  WHERE c.id = calc.counter_id AND month LIKE CONCAT('%', :month, '%')" +
            " AND c.id IN (:counterIds)", nativeQuery = true)
    List<Calculation> getCalculationsForMonthByCounters(@Param("counterIds") List<Long> counterIds, @Param("month") String month);
}
