package by.server.utility;

import by.server.models.DTO.ProductDTO;
import by.server.models.DTO.ProductionExpensesDTO;
import by.server.models.DTO.RealizationExpensesDTO;
import by.server.models.DTO.UserDTO;
import by.server.models.entities.*;
import by.server.models.enums.ResponseStatus;
import by.server.models.enums.Roles;
import by.server.models.tcp.Request;
import by.server.models.tcp.Response;
import by.server.service.ProductService;
import by.server.service.ProductionExpensesService;
import by.server.service.RealizationExpensesService;
import by.server.service.UserService;
import com.google.gson.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class ClientThread implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private UserService userService = new UserService();
    private ProductService productService = new ProductService();
    private RealizationExpensesService realizationExpensesService =  new RealizationExpensesService();
    private ProductionExpensesService productionExpensesService = new ProductionExpensesService();

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
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            user.setRole(new Role(user, Roles.USER));
                            userService.save(user);
                            response = new Response(ResponseStatus.OK, "Register", "");;
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), "");
                        }
                        break;
                    }
                    case LOGIN -> {
                        try {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            user = userService.findByUsernameOrEmailOrPassword(user.getUsername() == null ? user.getEmail() : user.getUsername(), user.getPassword());
                            response = new Response();
                            UserDTO returnUser = new UserDTO(user);
                            response = new Response(ResponseStatus.OK, "Login successfully!", gson.toJson(returnUser));
                            System.out.println(response);
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, e.getMessage(), null);
                        }
                        break;
                    }
                    case CALCULATE_PRODUCT_PRICE -> {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(request.getRequestMessage()).getAsJsonObject();
                            JsonElement productElement = jsonObject.get("product");
                            Product product = gson.fromJson(productElement, Product.class);
                            User user = userService.findById(product.getCreatedBy().getUserId());
                            product.setCreatedBy(user);
                            productService.save(product);
                            JsonElement realizationExpensesJSON = jsonObject.get("RealizationExpenses");
                            RealizationExpenses realizationExpenses = gson.fromJson(realizationExpensesJSON, RealizationExpenses.class);
                            realizationExpenses.setProduct(productService.findById(product.getProductId()));
                            realizationExpensesService.save(realizationExpenses);
                            JsonElement productionExpensesJSON = jsonObject.get("ProductionExpenses");
                            ProductionExpenses productionExpenses = gson.fromJson(productionExpensesJSON, ProductionExpenses.class);
                            productionExpenses.setProduct(productService.findById(product.getProductId()));
                            productionExpensesService.save(productionExpenses);
                            productService.calculateTotalPrice(product.getProductId());
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
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
