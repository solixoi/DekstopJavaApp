package by.client.models.tcp;

import by.client.models.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Request {
    private RequestType requestType;
    private String requestMessage;
}
