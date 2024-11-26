package by.server.utility;

import by.server.models.entities.Role;
import by.server.models.entities.User;
import by.server.models.enums.ResponseStatus;
import by.server.models.enums.Roles;
import by.server.models.tcp.Request;
import by.server.models.tcp.Response;
import by.server.service.UserService;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private UserService userService = new UserService();

    private Request request;
    private Response response;
    private Gson gson;

    public ClientThread(){

    }

    public ClientThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        request = new Request();
        response = new Response();
        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                String message = in.readLine();

                request = gson.fromJson(message, Request.class);

                switch (request.getRequestType()){
                    case REGISTER -> {
                        Role role = new Role();
                        User user = gson.fromJson(request.getRequestMessage(), User.class);
                        role.setRole(Roles.USER);
                        role.setUser(user);
                        user.setRole(role);
                        try {
                            userService.save(user);
                            response = new Response(ResponseStatus.OK, "Register", "");;
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), "");
                        }
                        break;
                    }
                    case LOGIN -> {
                        response = new Response(ResponseStatus.OK, "Login", "");
                        System.out.println("login");
                        break;
                    }
                }
                out.println(gson.toJson(response));
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Клиент: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " закрыл соединение.");
            try {
                clientSocket.close();
                in.close();
                out.close();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
