package by.client.models.entities;

import by.client.models.enums.Roles;
import lombok.*;
import java.sql.Timestamp;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Cloneable{
    private Long userId;
    private String password;
    private String username;
    private Timestamp dateCreation;
    private String email;
    private String firstName;
    private String lastName;
    private Roles role;

    @Override
    public User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public User(Long userId, Timestamp dateCreation, String email,  String firstName, String lastName, String password, String username) {
        this.userId = userId;
        this.dateCreation = dateCreation;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.username = username;
    }
}
