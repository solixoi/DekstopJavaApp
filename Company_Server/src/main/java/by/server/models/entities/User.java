package by.server.models.entities;

import by.server.models.DTO.UserDTO;
import by.server.models.enums.Roles;
import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.*;

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

    public User(String username, Timestamp dateCreation, String password, String email, String firstName, String lastName, Role role) {
        this.username = username;
        this.dateCreation = dateCreation;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Ban ban;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Log> logs;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private List<Product> products;

}
