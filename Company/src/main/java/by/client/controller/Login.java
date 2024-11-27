package by.client.controller;

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
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Map;

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

    @FXML
    public void onLoginButtonClicked() throws IOException {
        User user = new User();
        String login = textfieldLogin.getText();
        String password = passwordField.getText();
        user.setUsername(login);
        user.setPassword(password);
        Request request = new Request();
        request.setRequestType(RequestType.LOGIN);
        request.setRequestMessage(new Gson().toJson(user));
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        String answer = ClientSocket.getInstance().getIn().readLine();
        System.out.println(answer);
        if (answer != null) {
            Response response = new Gson().fromJson(answer, Response.class);
            if ( response.getStatus() != ResponseStatus.ERROR && response.getResponseData() != null) {
                JsonObject responseData = JsonParser.parseString(response.getResponseData()).getAsJsonObject();
                String roleString = responseData.get("roles").getAsString();
                Roles role = Roles.valueOf(roleString);
                JsonObject userJson = responseData.getAsJsonObject("user");
                User userData = new Gson().fromJson(userJson, User.class);
                userData.setRole(role);
                if (response.getStatus() == ResponseStatus.OK) {
                    labelMessage.setText("Успешный вход! " + userData.getUsername() + ";Ваша роль: " + userData.getRole());
                    ClientSocket.getInstance().setUser(userData);
                    labelMessage.setVisible(true);
                } else {
                    labelMessage.setText(response.getMessage());
                    labelMessage.setVisible(true);
                }
            } else {
                labelMessage.setText(response.getMessage());
                labelMessage.setVisible(true);
            }
        } else {
            labelMessage.setText("Нет ответа от сервера.");
            labelMessage.setVisible(true);
        }
    }

}
