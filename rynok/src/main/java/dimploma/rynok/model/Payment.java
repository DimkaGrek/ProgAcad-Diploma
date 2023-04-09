package dimploma.rynok.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name="Payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "counter_id")
    private Counter counter;

    @ManyToOne
    @JoinColumn(name = "calc_id")
    private Calculation calculation;

    @Column(name = "receipt_id")
    private Long receiptId;

    private Double amount;

    @Nullable
    private Double debt;

    @Nullable
    private Double overpayment;

    private LocalDateTime date;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser user;

    public Payment(Long receiptId, Double amount, Double debt, Double overpayment, LocalDateTime date, String notes) {
        this.receiptId = receiptId;
        this.amount = amount;
        this.debt = debt;
        this.overpayment = overpayment;
        this.date = date;
        this.notes = notes;
    }
}
