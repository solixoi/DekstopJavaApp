package by.server.models.entities;

import by.server.models.enums.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ban")
public class Ban {

    public Ban(User user) {
        this.user = user;
    }

    public Ban(User user, boolean isBanned) {
        this.user = user;
        this.isBanned = isBanned;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="isban")
    private boolean isBanned = false;

    @OneToOne(cascade = CascadeType.REMOVE)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}
