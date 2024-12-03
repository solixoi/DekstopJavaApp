package by.client.controller;

import by.client.models.entities.Product;
import by.client.models.entities.ProductionExpenses;
import by.client.models.entities.RealizationExpenses;
import by.client.models.entities.User;
import by.client.models.enums.RequestType;
import by.client.models.enums.ResponseStatus;
import by.client.models.enums.Roles;
import by.client.models.tcp.Request;
import by.client.models.tcp.Response;
import by.client.utility.ClientSocket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.math.BigDecimal;

public class Login {

    @FXML
    private TextField textfieldLogin;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label labelMessage;

    @FXML
    public void initialize() {
        // Инициализация, если необходимо
    }

//    @FXML
//    public void onLoginButtonClicked() throws IOException {
//        User user = new User();
//        String login = textfieldLogin.getText();
//        String password = passwordField.getText();
//        user.setUsername(login);
//        user.setPassword(password);
//        Request request = new Request();
//        request.setRequestType(RequestType.LOGIN);
//        request.setRequestMessage(new Gson().toJson(user));
//        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
//        String answer = ClientSocket.getInstance().getIn().readLine();
//        if (answer != null) {
//            Response response = new Gson().fromJson(answer, Response.class);
//            if ( response.getStatus() != ResponseStatus.ERROR && response.getResponseData() != null) {
//                User responseUser = new Gson().fromJson(response.getResponseData(), User.class);
//                if (response.getStatus() == ResponseStatus.OK) {
//                    labelMessage.setText("Успешный вход! " + responseUser.getUsername() + ";Ваша роль: " + responseUser.getRole());
//                    ClientSocket.getInstance().setUser(responseUser);
//                    labelMessage.setVisible(true);
//                } else {
//                    labelMessage.setText(response.getMessage());
//                    labelMessage.setVisible(true);
//                }
//            } else {
//                labelMessage.setText(response.getMessage());
//                labelMessage.setVisible(true);
//            }
//        } else {
//            labelMessage.setText("Нет ответа от сервера.");
//            labelMessage.setVisible(true);
//        }
//    }

    @FXML
    public void onLoginButtonClicked() throws IOException {
        Request request = new Request();
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        request.setRequestType(RequestType.CALCULATE_PRODUCT_PRICE);

        User user = new User();
        user.setId(1L);
        Product product = new Product("name1", BigDecimal.valueOf(100L), BigDecimal.valueOf(100L),
                BigDecimal.valueOf(100L), BigDecimal.valueOf(100L), user);
        RealizationExpenses realizationExpenses = new RealizationExpenses(product, BigDecimal.valueOf(100L), BigDecimal.valueOf(100L),
                BigDecimal.valueOf(100L), BigDecimal.valueOf(100L));
        ProductionExpenses productionExpenses = new ProductionExpenses(product, BigDecimal.valueOf(100L), BigDecimal.valueOf(100L),
                BigDecimal.valueOf(100L), BigDecimal.valueOf(100L));
        jsonObject.add("product", gson.toJsonTree(product));
        jsonObject.add("RealizationExpenses", gson.toJsonTree(realizationExpenses));
        jsonObject.add("ProductionExpenses", gson.toJsonTree(productionExpenses));
        request.setRequestMessage(jsonObject.toString());
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        String answer = ClientSocket.getInstance().getIn().readLine();

        Response res = gson.fromJson(answer, Response.class);
        System.out.println(res.getMessage() + " " + res.getResponseData());
    }
}
