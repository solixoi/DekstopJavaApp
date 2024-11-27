package by.client.models.entities;

import by.client.models.enums.Roles;
import lombok.*;

import javax.management.relation.Role;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    private String username;
    private String password;
    private java.sql.Timestamp dateCreation;
    private String email;
    private String firstName;
    private String lastName;
    private Roles role;
}
