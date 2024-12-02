package by.server.models.DTO;

import by.server.models.enums.Roles;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private long id;
    private String password;
    private String username;
    private Timestamp dataCreation;
    private String email;
    private String firstName;
    private String lastName;
    private Roles role;
}
