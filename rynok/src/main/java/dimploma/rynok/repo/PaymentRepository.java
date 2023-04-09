package dimploma.rynok.repo;

import dimploma.rynok.model.Counter;
import dimploma.rynok.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.counter = :field")
    Double getSumPaymentsByCounter(@Param("field") Counter counterId);
}
