package by.client.controller;

import by.client.models.entities.User;
import by.client.models.enums.RequestType;
import by.client.models.enums.ResponseStatus;
import by.client.models.tcp.Request;
import by.client.models.tcp.Response;
import by.client.utility.ClientSocket;
import by.client.utility.ValidationUtils;
import com.google.gson.Gson;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
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
    private Button buttonLogin;
    @FXML
    private PasswordField passwordfieldPasswordEquals;
    @FXML
    private Label labelMessage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onRegisterButtonClicked(ActionEvent event) throws IOException {
        if (!ValidationUtils.validateName(textfieldFirstName.getText())) {
            showMessage("Ошибка валидации имени: допустимы только буквы, длина от 2 до 15 символов.", "error");
            return;
        }
        if (!ValidationUtils.validateSurname(textfieldLastName.getText())) {
            showMessage("Ошибка валидации имени: допустимы только буквы, длина от 2 до 15 символов.", "error");
            return;
        }
        if (!ValidationUtils.validateLogin(textfieldUsername.getText())) {
            showMessage("Ошибка валидации логина: длина от 4 до 14 символов.", "error");
            return;
        }
        if (!ValidationUtils.validateEmail(textfieldEmail.getText())) {
            showMessage("Ошибка валидации электронной почты: формат example@test.com.", "error");
            return;
        }
        if (!ValidationUtils.validatePassword(passwordfieldPassword.getText())) {
            showMessage("Пароль должен быть не менее 8 символов, содержать буквы и цифры.", "error");
            return;
        }
        if (!passwordfieldPassword.getText().equals(passwordfieldPasswordEquals.getText())) {
            showMessage("Пароли не совпадают.", "error");
            return;
        }
        Request request = new Request();
        User user = new User();
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
            showMessage("Successfully registered!", "success");
        } else {
            showMessage(response.getMessage(), "error");
        }
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/Login.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) buttonLogin.getScene().getWindow();
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
