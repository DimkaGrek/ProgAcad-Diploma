package dimploma.rynok.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name="Counters")
public class Counter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_pavilion")
    private String pavilion;

    @Column(name = "id_place")
    private String place;

    @Column(name = "counter_type")
    private String type;

    @Column(name = "counter_number")
    private Long number;

    @Column(name = "counter_atomicity")
    private Long atomicity;

    @Column(name = "date_manufac", columnDefinition = "DATE")
    private LocalDate dateManufac;

    @Column(name = "date_install", columnDefinition = "DATE")
    private LocalDate dateInstall;

    @ManyToOne
    @JoinColumn(name = "id_arendator")
    private Arendator arendator;

    private String notes;

    @Column(name = "parent_id")
    private Long parent;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @ManyToMany(mappedBy = "counters")
    private List<CustomUser> users;

    @ManyToMany(mappedBy = "counters")
    private List<Group> groups;

    @OneToMany(mappedBy = "counter", cascade = CascadeType.PERSIST)
    private List<Calculation> calculations = new ArrayList<>();

    @OneToMany(mappedBy = "counter", cascade = CascadeType.PERSIST)
    private List<Payment> payments = new ArrayList<>();

    public Counter(String pavilion, String place, String type, Long number, Long atomicity, LocalDate dateManufac, LocalDate dateInstall, String notes, Long parent) {
        this.pavilion = pavilion;
        this.place = place;
        this.type = type;
        this.number = number;
        this.atomicity = atomicity;
        this.dateManufac = dateManufac;
        this.dateInstall = dateInstall;
        this.notes = notes;
        this.parent = parent;
    }

}
