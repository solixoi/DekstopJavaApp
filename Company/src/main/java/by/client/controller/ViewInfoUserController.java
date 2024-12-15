package by.client.controller;

import by.client.models.entities.User;
import by.client.models.enums.RequestType;
import by.client.models.enums.ResponseStatus;
import by.client.models.enums.Roles;
import by.client.models.tcp.Request;
import by.client.models.tcp.Response;
import by.client.utility.ClientSocket;
import by.client.utility.Information;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ViewInfoUserController implements Initializable {
    @FXML
    private TextField idField, emailField, loginField, firstNameField, lastNameField, dateCreationField;

    @FXML
    private ComboBox<String> roleField;

    @FXML
    private Text controlProductNavigation, helloText, clickedLogout, mainPageNavigation;

    @FXML
    private Label labelMessage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = Information.getInstance().getUser();
        helloText.setText(String.format("Профиль пользователя с id: %d!!!", user.getUserId()));
        emailField.setText(user.getEmail());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        loginField.setText(user.getUsername());
        idField.setText(user.getUserId().toString());
        LocalDate creationDate = user.getDateCreation().toLocalDateTime().toLocalDate();
        dateCreationField.setText(creationDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        roleField.setValue(user.getRole().toString());
    }

    @FXML
    public void clickedBackButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/AdminPage.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) controlProductNavigation.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    public void clickedLogout() {
        ClientSocket.getInstance().setUser(null);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/Login.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);
            newStage.show();
            Stage currentStage = (Stage) clickedLogout.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void clickedMainPage(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/MainPage.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) mainPageNavigation.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    public void clickedSubmitChangeRole(ActionEvent event) throws IOException {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.CHANGE_ROLE);
            JsonObject jsonObject = new JsonObject();
            User userAdmin = ClientSocket.getInstance().getUser().clone();
            userAdmin.setRole(null);
            jsonObject.add("user", new Gson().toJsonTree(userAdmin));
            jsonObject.addProperty("user_id", Long.valueOf(idField.getText()));
            jsonObject.addProperty("role", Roles.valueOf(roleField.getValue()).name());
            request.setRequestMessage(new Gson().toJson(jsonObject));
            ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            if (response.getStatus() == ResponseStatus.OK) {
                showMessage(response.getMessage(), "success");
            } else {
                showMessage(response.getMessage(), "error");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showMessage(e.getMessage(), "error");
        }
    }

    @FXML
    public void clickedControlProductNavigation(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/PricesPage.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) controlProductNavigation.getScene().getWindow();
        currentStage.close();
    }

    private void showMessage(String message, String type) {
        labelMessage.setText(message);
        labelMessage.getStyleClass().removeAll("success", "error");
        labelMessage.getStyleClass().add(type);
        labelMessage.setVisible(true);
        labelMessage.toFront();
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(3),
                event -> labelMessage.setVisible(false)
        ));
        timeline.play();
    }
}
