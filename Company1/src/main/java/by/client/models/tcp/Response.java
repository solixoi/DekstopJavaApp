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
    private byte[] data;

    public Response(ResponseStatus status, String message, String responseData) {
        this.status = status;
        this.message = message;
        this.responseData = responseData;
    }
}
