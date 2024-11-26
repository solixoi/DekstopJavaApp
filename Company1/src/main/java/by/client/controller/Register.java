package by.client.controller;

import by.client.models.entities.User;
import by.client.models.enums.RequestType;
import by.client.models.enums.ResponseStatus;
import by.client.models.tcp.Request;
import by.client.models.tcp.Response;
import by.client.utility.ClientSocket;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Register {
    @FXML
    private TextField textfieldFirstName;
    @FXML
    private TextField textfieldLastName;
    @FXML
    private TextField textfieldUsername;
    @FXML
    private TextField textfieldEmail;
    @FXML
    private PasswordField passwordfieldPassword;
    @FXML
    private Button buttonCancel;
    @FXML
    private PasswordField passwordfieldPasswordEquals;
    @FXML
    private Label labelMessage;

    @FXML
    public void initialize() {
        System.out.println("Controller initialized!");
    }

    @FXML
    public void onRegisterButtonClicked(ActionEvent event) throws IOException {
        Request request = new Request();
        User user = new User();
        if (!passwordfieldPassword.getText().equals(passwordfieldPasswordEquals.getText())) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }
        user.setFirstName(textfieldFirstName.getText());
        user.setLastName(textfieldLastName.getText());
        user.setUsername(textfieldUsername.getText());
        user.setEmail(textfieldEmail.getText());
        user.setPassword(passwordfieldPassword.getText());
        request.setRequestMessage(new Gson().toJson(user));
        request.setRequestType(RequestType.REGISTER);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        String answer = ClientSocket.getInstance().getIn().readLine();
        Response response = new Gson().fromJson(answer, Response.class);
        if (response.getStatus() == ResponseStatus.OK) {
            labelMessage.setText("Successfully registered!");
            labelMessage.setVisible(true);
        } else {
            labelMessage.setText("Failed to register!");
            labelMessage.setVisible(true);
        }
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) buttonCancel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
