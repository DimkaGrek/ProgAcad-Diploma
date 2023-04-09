package dimploma.rynok.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name="Groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser user;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "group_counters",
            joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "counter_id", referencedColumnName = "id")
    )
    private List<Counter> counters;

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
