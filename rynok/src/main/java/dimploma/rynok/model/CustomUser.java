package dimploma.rynok.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@NamedEntityGraph(name = "User.roles", attributeNodes = @NamedAttributeNode("roles"))
@Table(name = "users")
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    private String email;

    @Column(nullable = false)
    private String realname;

    private String position;

    private String phone;

    private String notes;

    private Long permission;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private List<Roles> roles;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_counters",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "counter_id", referencedColumnName = "id")
    )
    private List<Counter> counters;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Group> groups = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Calculation> calculations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Payment> payments = new ArrayList<>();

//    @OneToMany(mappedBy = "token", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Token> tokens = new ArrayList<>();

    public CustomUser(String login, String password, String email, String realname, String position, String phone, String notes, Long permission) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.realname = realname;
        this.position = position;
        this.phone = phone;
        this.notes = notes;
        this.permission = permission;
    }

    public void addGroup(Group group) {
        if (! groups.contains(group)) {
            groups.add(group);
            group.setUser(this);
        }
    }

//    public void addToken(Token token) {
//        if ( ! tokens.contains(token)) {
//            tokens.add(token);
//            token.setUser(this);
//        }
//    }

//    public List<Token> getTokens() {
//        return tokens;
//    }
}