package by.server.models.DTO;

import by.server.models.entities.User;
import by.server.models.enums.Roles;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.dateCreation = user.getDateCreation();
        this.role = user.getRole().getRole();
    }

    private Long userId;
    private String password;
    private String username;
    private Timestamp dateCreation;
    private String email;
    private String firstName;
    private String lastName;
    private Roles role;
}
