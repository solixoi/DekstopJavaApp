package by.server.models.DTO;

import by.server.models.entities.Log;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogDTO {
    private UserDTO user;
    private String action;

    public LogDTO(Log log) {
        this.user = new UserDTO(log.getUser());
        this.action = log.getAction();
    }
}
