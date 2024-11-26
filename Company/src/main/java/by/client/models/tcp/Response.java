package by.client.models.tcp;

import by.client.models.enums.ResponseStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Response {
    private ResponseStatus status;
    private String message;
    private String responseData;
}
