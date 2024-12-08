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
        this.id = user.getUserId();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.dataCreation = user.getDateCreation();
        this.role = user.getRole().getRole();
    }

    private Long id;
    private String password;
    private String username;
    private Timestamp dataCreation;
    private String email;
    private String firstName;
    private String lastName;
    private Roles role;
}
