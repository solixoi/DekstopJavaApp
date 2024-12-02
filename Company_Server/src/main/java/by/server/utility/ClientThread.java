package by.server.utility;

import by.server.models.DTO.UserDTO;
import by.server.models.entities.Role;
import by.server.models.entities.User;
import by.server.models.enums.ResponseStatus;
import by.server.models.enums.Roles;
import by.server.models.tcp.Request;
import by.server.models.tcp.Response;
import by.server.service.UserService;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


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
        gson = new GsonBuilder().create();;
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                String message = in.readLine();

                request = gson.fromJson(message, Request.class);

                switch (request.getRequestType()){
                    case REGISTER -> {
                        UserDTO user = gson.fromJson(request.getRequestMessage(), UserDTO.class);
                        user.setRole(Roles.USER);
                        try {
                            userService.save(new User(user));
                            response = new Response(ResponseStatus.OK, "Register", "");;
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), "");
                        }
                        break;
                    }
                    case LOGIN -> {
                        try {
                            UserDTO userDTO = gson.fromJson(request.getRequestMessage(), UserDTO.class);
                            User user = new User(userDTO);
                            User returnUser = userService.findByUsernameOrEmailOrPassword(user.getUsername() == null ? user.getEmail() : user.getUsername(), user.getPassword());
                            response = new Response();
                            Map<String, Object> responseData = new HashMap<>();
                            responseData.put("roles", returnUser.getRole().getRole());
                            returnUser.setLogs(null);
                            returnUser.setProducts(null);
                            returnUser.setRole(null);
                            responseData.put("user", returnUser);
                            response = new Response(ResponseStatus.OK, "Login successfully!", gson.toJson(responseData));
                            System.out.println(response);
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
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
