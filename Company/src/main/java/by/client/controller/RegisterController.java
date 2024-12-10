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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class RegisterController {
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
    private Label labelMessage1;
    @FXML
    private GridPane formContainer;

    @FXML
    public void initialize() {
        BoxBlur blur = new BoxBlur();
        blur.setWidth(50);
        blur.setHeight(50);
        blur.setIterations(20);
        formContainer.setEffect(blur);
    }

    @FXML
    public void onRegisterButtonClicked(ActionEvent event) throws IOException {
        Request request = new Request();
        User user = new User();
        if (!passwordfieldPassword.getText().equals(passwordfieldPasswordEquals.getText())) {
            showMessage("Пароли не совпадают", "error");
            return;
        }
        user.setFirstName(textfieldFirstName.getText());
        user.setLastName(textfieldLastName.getText());
        user.setUsername(textfieldUsername.getText());
        user.setEmail(textfieldEmail.getText());
        user.setPassword(passwordfieldPassword.getText());
        request.setRequestMessage(new Gson().toJson(user));
        request.setRequestType(RequestType.REGISTER);
        System.out.println(user);
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
        Stage currentStage = (Stage) buttonCancel.getScene().getWindow();
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
