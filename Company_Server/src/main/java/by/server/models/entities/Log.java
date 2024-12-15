package by.server.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logs_id")
    private Long logsId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "action", nullable = false, length = 300)
    private String action;

    public Log(User user, String action) {
        this.user = user;
        this.action = action;
    }

    @Column(name = "date_creation")
    private Timestamp dateCreation;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            long currentTimeInSeconds = System.currentTimeMillis() / 1000 * 1000;
            dateCreation = new Timestamp(currentTimeInSeconds);
        }
    }
}
