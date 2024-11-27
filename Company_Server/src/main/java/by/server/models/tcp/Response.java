package by.server.models.tcp;

import by.server.models.enums.ResponseStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
@Setter
public class Response {
    private ResponseStatus status;
    private String message;
    private String responseData;
}
