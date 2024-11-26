package by.client.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username;
    private String password;
    private java.sql.Timestamp dateCreation;
    private String email;
    private String firstName;
    private String lastName;
}
