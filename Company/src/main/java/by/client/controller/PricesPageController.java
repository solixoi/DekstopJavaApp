package by.client.controller;

import by.client.models.entities.Product;
import by.client.models.entities.ProductionExpenses;
import by.client.models.entities.RealizationExpenses;
import by.client.models.entities.User;
import by.client.models.enums.RequestType;
import by.client.models.enums.ResponseStatus;
import by.client.models.enums.Roles;
import by.client.models.tcp.Request;
import by.client.models.tcp.Response;
import by.client.utility.ClientSocket;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class PricesPageController implements Initializable {

    @FXML
    private Text clickedLogout, mainPageNavigation, adminPageNavigation, allPricesPageNavigation, reportPageNavigation;

    @FXML
    private TextField productNameField, plannedRevenueField, materialCostField, overheadCostField, wagesCostField,
            otherExpensesProductionField, distributionCostField, marketingCostField, transportationCostField, otherExpensesRealizationField;

    @FXML
    private Label labelMessage;

    @FXML
    private Button usernameButton;

    @FXML
    private ContextMenu contextMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadContextMenu();
        User user = ClientSocket.getInstance().getUser();
        if (user.getRole().equals(Roles.ADMIN)) {
            adminPageNavigation.setVisible(true);
            reportPageNavigation.setVisible(true);
        } else if (user.getRole().equals(Roles.MANAGER)) {
            reportPageNavigation.setVisible(true);
        }
        usernameButton.setText(user.getUsername());
    }

    @FXML
    public void clickedButtonSubmitPrice(ActionEvent event) throws IOException {
        Request request = new Request();

        JsonObject jsonObject = new JsonObject();
        User user = ClientSocket.getInstance().getUser().clone();
        user.setRole(null);


        Product sendProduct = new Product(user, productNameField.getText(), BigDecimal.valueOf(Long.parseLong(plannedRevenueField.getText())));
        jsonObject.add("product", new Gson().toJsonTree(sendProduct));

        ProductionExpenses productionExpenses = new ProductionExpenses( BigDecimal.valueOf(Long.parseLong(wagesCostField.getText())), BigDecimal.valueOf(Long.parseLong(materialCostField.getText())),
                BigDecimal.valueOf(Long.parseLong(overheadCostField.getText())),  BigDecimal.valueOf(Long.parseLong(otherExpensesProductionField.getText())));
        jsonObject.add("ProductionExpenses", new Gson().toJsonTree(productionExpenses));


        RealizationExpenses realizationExpenses = new RealizationExpenses( BigDecimal.valueOf(Long.parseLong(marketingCostField.getText())), BigDecimal.valueOf(Long.parseLong(distributionCostField.getText())),
                BigDecimal.valueOf(Long.parseLong(transportationCostField.getText())),  BigDecimal.valueOf(Long.parseLong(otherExpensesRealizationField.getText())));
        jsonObject.add("RealizationExpenses", new Gson().toJsonTree(realizationExpenses));

        request.setRequestMessage(new Gson().toJson(jsonObject));
        request.setRequestType(RequestType.CALCULATE_PRODUCT_PRICE);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        String answer = ClientSocket.getInstance().getIn().readLine();
        Response response = new Gson().fromJson(answer, Response.class);
        if (response.getStatus() == ResponseStatus.OK) {
            showMessage(response.getMessage(), "success");
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
    private void showContextMenu(MouseEvent event) {
        contextMenu.show(usernameButton.getScene().getWindow(), 2415, 95);
    }

    private void loadContextMenu(){
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
            Stage currentStage = (Stage) mainPageNavigation.getScene().getWindow();
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
        labelMessage.toFront();
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(3),
                event -> labelMessage.setVisible(false)
        ));
        timeline.play();
    }
}
