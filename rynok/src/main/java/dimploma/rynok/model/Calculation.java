package dimploma.rynok.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name="Calculation")
public class Calculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "counter_id")
    private Counter counter;

    private String month;

    @Column(name = "count_now")
    private Long countNow;

    @Column(name = "count_before")
    private Long countBefore;

    private Long difference;

    private Double rate;

    private Double amount;

    @Nullable
    private Double debt;

    @Nullable
    private Double overpayment;

    private LocalDateTime date;

    @Nullable
    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser user;

    @ManyToOne
    @JoinColumn(name = "id_arendator")
    private Arendator arendator;

    @OneToMany(mappedBy = "calculation", cascade = CascadeType.PERSIST)
    private List<Payment> payments = new ArrayList<>();

    public Calculation(String month, Long countNow, Long countBefore, Long difference, Double rate, Double amount,
                       Double debt, Double overpayment, LocalDateTime date, String notes) {
        this.month = month;
        this.countNow = countNow;
        this.countBefore = countBefore;
        this.difference = difference;
        this.rate = rate;
        this.amount = amount;
        this.debt = debt;
        this.overpayment = overpayment;
        this.date = date;
        this.notes = notes;
    }
}
