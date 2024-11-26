package by.server.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 150)
    private String password;

    @Column(name = "date_creation")
    private Timestamp dateCreation;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            long currentTimeInSeconds = System.currentTimeMillis() / 1000 * 1000;
            dateCreation = new Timestamp(currentTimeInSeconds);
        }
    }

    @Column(name = "email", nullable = false, length = 20)
    private String email;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Log> logs;

    @OneToMany(mappedBy = "createdBy")
    private List<Product> products;
}
