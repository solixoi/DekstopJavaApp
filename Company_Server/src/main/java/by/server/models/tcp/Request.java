package by.server.models.tcp;

import by.server.models.enums.RequestType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
@Setter
public class Request {
    private RequestType requestType;
    private String requestMessage;
}
