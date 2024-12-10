package by.client.controller;

import by.client.models.entities.User;
import by.client.models.enums.RequestType;
import by.client.models.enums.ResponseStatus;
import by.client.models.tcp.Request;
import by.client.models.tcp.Response;
import by.client.utility.ClientSocket;
import com.google.gson.Gson;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField textfieldLogin;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label labelMessage;

    @FXML
    private Button buttonRegister;

    @FXML
    public void initialize() {

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
                    showMessage("Successfully login!", "success");
                    ClientSocket.getInstance().setUser(responseUser);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/MainPage.fxml"));
                    Parent mainPage = loader.load();
                    Stage newStage = new Stage();
                    newStage.setScene(new Scene(mainPage));
                    newStage.setMaximized(true);
                    newStage.show();
                    Stage currentStage = (Stage) buttonRegister.getScene().getWindow();
                    currentStage.close();
                } else {
                    showMessage(response.getMessage(), "error");
                }
            } else {
                showMessage(response.getMessage(), "error");
            }
        } else {
            showMessage("Нет ответа от сервера!", "error");
        }
    }

    @FXML
    public void onRegisterButtonClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/Registration.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) buttonRegister.getScene().getWindow();
        currentStage.close();
    }

    private void showMessage(String message, String type) {
        labelMessage.setText(message);
        labelMessage.getStyleClass().removeAll("success", "error");
        labelMessage.getStyleClass().add(type);
        labelMessage.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(3),
                event -> labelMessage.setVisible(false)
        ));
        timeline.play();
    }
}

