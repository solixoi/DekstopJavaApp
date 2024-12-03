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
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

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
        if (answer != null) {
            Response response = new Gson().fromJson(answer, Response.class);
            if ( response.getStatus() != ResponseStatus.ERROR && response.getResponseData() != null) {
                User responseUser = new Gson().fromJson(response.getResponseData(), User.class);
                if (response.getStatus() == ResponseStatus.OK) {
                    labelMessage.setText("Успешный вход! " + responseUser.getUsername() + ";Ваша роль: " + responseUser.getRole());
                    ClientSocket.getInstance().setUser(responseUser);
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
