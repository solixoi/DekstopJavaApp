package by.client.controller;

import by.client.models.entities.Product;
import by.client.models.entities.ProductionExpenses;
import by.client.models.entities.RealizationExpenses;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import by.client.models.entities.User;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private Text clickedLogout, helloLabel, pricesPageNavigation, adminPageNavigation, allPricesPageNavigation, reportPageNavigation;

    @FXML
    private Label labelMessage, dateCreationAccount, dayRegisterAccount, countProductsLabel, countEditProductsLabel;

    @FXML
    private Button usernameButton;

    @FXML
    private ContextMenu contextMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadContextMenu();
        User user = ClientSocket.getInstance().getUser();
        refreshLoadData();
        if (user.getRole().equals(Roles.ADMIN)) {
            adminPageNavigation.setVisible(true);
            reportPageNavigation.setVisible(true);
        } else if (user.getRole().equals(Roles.MANAGER)) {
            reportPageNavigation.setVisible(true);
        }
        LocalDate creationDate = user.getDateCreation().toLocalDateTime().toLocalDate();
        dateCreationAccount.setText(creationDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));
        dayRegisterAccount.setText(ChronoUnit.DAYS.between(creationDate, LocalDate.now()) + " дней");
        usernameButton.setText(user.getUsername());
        helloLabel.setText(String.format("Добро пожаловать, %s!", user.getUsername()));
        countProductsLabel.setText(Information.getInstance().getCountProducts() != null ? String.valueOf(Information.getInstance().getCountProducts()) + " раз" : "0 раз");
        countEditProductsLabel.setText(Information.getInstance().getCountEditProducts() != null ? String.valueOf(Information.getInstance().getCountEditProducts()) + " раз" : "0 раз");
    }

    private void refreshLoadData() {
        try {
            Request request = new Request();

            User user = ClientSocket.getInstance().getUser().clone();
            user.setRole(null);

            request.setRequestMessage(new Gson().toJson(user));
            request.setRequestType(RequestType.GET_LOAD_DATA_MAIN_PAGE);
            ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            JsonObject jsonObject = new Gson().fromJson(response.getResponseData(), JsonObject.class);
            Long countProducts = jsonObject.has("countProducts") ? jsonObject.get("countProducts").getAsLong() : 0L;
            Long countEditProductData = jsonObject.has("countEditProductData") ? jsonObject.get("countEditProductData").getAsLong() : 0L;
            Information.getInstance().setProductCounts(countProducts, countEditProductData);
            if (response.getStatus() == ResponseStatus.ERROR) {
                showMessage(response.getMessage(), "error");
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
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
    private void showContextMenu(MouseEvent event) {
        contextMenu.show(usernameButton.getScene().getWindow(), 2415, 95);
    }

    private void loadContextMenu() {
        contextMenu = new ContextMenu();
        MenuItem profileItem = new MenuItem("Профиль");
        profileItem.setOnAction(event -> clickedUserProfilePage());
        MenuItem logoutItem = new MenuItem("Выйти");
        logoutItem.setOnAction(event -> clickedLogout());
        contextMenu.getItems().addAll(profileItem, logoutItem);

        contextMenu.getStyleClass().add("context-menu");
        profileItem.getStyleClass().add("menu-item");
        logoutItem.getStyleClass().add("menu-item");

        contextMenu.setAutoHide(true);
    }

    @FXML
    private void clickedUserProfilePage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/ProfileUserPage.fxml"));
        try {
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);
            newStage.show();
            Stage currentStage = (Stage) pricesPageNavigation.getScene().getWindow();
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
