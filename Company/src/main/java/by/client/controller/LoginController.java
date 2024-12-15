package by.client.controller;

import by.client.models.entities.User;
import by.client.models.enums.RequestType;
import by.client.models.enums.ResponseStatus;
import by.client.models.tcp.Request;
import by.client.models.tcp.Response;
import by.client.utility.ClientSocket;
import by.client.utility.Information;
import by.client.utility.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField textfieldLogin;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label labelMessage;

    @FXML
    private Button buttonRegister;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onLoginButtonClicked() throws IOException {
        User user = new User();
        if (!ValidationUtils.isEmail(textfieldLogin.getText())) {
            if (!ValidationUtils.validateLogin(textfieldLogin.getText())) {
                showMessage("Некорректный ввод: допустимы только email или username.", "error");
                return;
            } else {
                user.setUsername(textfieldLogin.getText());
            }
        } else {
            user.setEmail(textfieldLogin.getText());
        }
        if (!ValidationUtils.validatePassword(passwordField.getText())) {
            showMessage("Пароль должен быть не менее 8 символов, содержать буквы и цифры.", "error");
            return;
        }
        String password = passwordField.getText();
        user.setPassword(password);

        Request request = new Request();
        request.setRequestType(RequestType.LOGIN);
        request.setRequestMessage(new Gson().toJson(user));
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        String answer = ClientSocket.getInstance().getIn().readLine();
        if (answer != null) {
            Response response = new Gson().fromJson(answer, Response.class);
            if (response.getStatus() != ResponseStatus.ERROR && response.getResponseData() != null) {
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

