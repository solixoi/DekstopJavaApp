package by.client.controller;

import by.client.models.entities.Product;
import by.client.models.entities.User;
import by.client.models.enums.RequestType;
import by.client.models.enums.ResponseStatus;
import by.client.models.enums.Roles;
import by.client.models.tcp.Request;
import by.client.models.tcp.Response;
import by.client.utility.ClientSocket;
import by.client.utility.CustomTableCellFactory;
import by.client.utility.Information;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ProfilePageController implements Initializable {
    @FXML
    private TextField roleField, emailField, loginField, firstNameField, lastNameField, dateCreationField;

    @FXML
    private PasswordField passwordField, acceptPasswordField;

    @FXML
    private Text allPricesPageNavigation, helloText, clickedLogout, mainPageNavigation, pricesPageNavigation, adminPageNavigation, reportPageNavigation;

    @FXML
    private Label labelMessage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = ClientSocket.getInstance().getUser();
        if (user.getRole().equals(Roles.ADMIN)) {
            adminPageNavigation.setVisible(true);
            reportPageNavigation.setVisible(true);
        } else if (user.getRole().equals(Roles.MANAGER)) {
            reportPageNavigation.setVisible(true);
        }
        helloText.setText(String.format("Добро пожаловать, %s %s в ваш профиль!!!", user.getFirstName(), user.getLastName()));
        emailField.setText(user.getEmail());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        loginField.setText(user.getUsername());
        LocalDate creationDate = user.getDateCreation().toLocalDateTime().toLocalDate();
        dateCreationField.setText(creationDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        roleField.setText(user.getRole().toString());
    }

    @FXML
    public void clickedSubmitUserButton(ActionEvent event) throws IOException {
        User user = new User(ClientSocket.getInstance().getUser().getUserId(), ClientSocket.getInstance().getUser().getDateCreation(), emailField.getText(), firstNameField.getText(), lastNameField.getText(),
                (passwordField.getText() != null && passwordField.getText().trim().isEmpty()) ? ClientSocket.getInstance().getUser().getPassword() : passwordField.getText() , loginField.getText());
        Request request = new Request();
        request.setRequestType(RequestType.UPDATE_USER_ACCOUNT);
        request.setRequestMessage(new Gson().toJson(user));
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        String answer = ClientSocket.getInstance().getIn().readLine();
        Response response = new Gson().fromJson(answer, Response.class);
        if(response.getStatus() == ResponseStatus.OK){
            showMessage(response.getMessage(), "success");
            ClientSocket.getInstance().setUser(new Gson().fromJson(response.getResponseData(), User.class));
        } else {
            showMessage(response.getMessage(), "error");
        }
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
    public void clickedPricesPage(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/PricesPage.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) pricesPageNavigation.getScene().getWindow();
        currentStage.close();
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
    public void clickedAdminPageNavigation(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/AdminPage.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) adminPageNavigation.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    public void clickedAllPricesNavigation(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/AllPricesPageNavigation.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) allPricesPageNavigation.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    public void clickedReportPageNavigation(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/ReportPage.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) reportPageNavigation.getScene().getWindow();
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
