package by.client.models.entities;

import by.client.models.enums.Roles;
import lombok.*;
import java.sql.Timestamp;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    private String password;
    private String username;
    private Timestamp dateCreation;
    private String email;
    private String firstName;
    private String lastName;
    private Roles role;
}
