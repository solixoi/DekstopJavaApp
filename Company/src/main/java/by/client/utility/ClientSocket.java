package by.client.utility;

import by.client.models.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Getter
@Setter
public class ClientSocket {
    private static final ClientSocket INSTANCE = new ClientSocket();
    private static Socket socket;
    private BufferedReader in;
    private static User user;
    private PrintWriter out;

    private ClientSocket() {
        try {
            socket = new Socket("127.0.0.1", 6666);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static ClientSocket getInstance() {
        return INSTANCE;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        ClientSocket.socket = socket;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
