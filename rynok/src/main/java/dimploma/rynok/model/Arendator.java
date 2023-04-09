package dimploma.rynok.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name="Arendators")
public class Arendator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private String evidence;

    private String passport;

    private String address;

    @Column(name = "type_company")
    private String typeCompany;

    private String phone1;

    private String phone2;

    private String notes;

    private String email;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser user;

    @OneToMany(mappedBy = "arendator", cascade = CascadeType.PERSIST)
    private List<Counter> counters = new ArrayList<>();

    public Arendator(String name, String surname, String evidence, String passport, String address,
                     String typeCompany, String phone1, String phone2, String notes, String email) {
        this.name = name;
        this.surname = surname;
        this.evidence = evidence;
        this.passport = passport;
        this.address = address;
        this.typeCompany = typeCompany;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.notes = notes;
        this.email = email;
    }

    public void addCounter(Counter counter) {
        if( ! counters.contains(counter)) {
            counters.add(counter);
            counter.setArendator(this);
        }
    }

}
