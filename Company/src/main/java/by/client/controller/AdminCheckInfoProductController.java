package by.client.controller;

import by.client.models.entities.Product;
import by.client.models.entities.ProductionExpenses;
import by.client.models.entities.RealizationExpenses;
import by.client.models.entities.User;
import by.client.models.enums.RequestType;
import by.client.models.enums.ResponseStatus;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminCheckInfoProductController implements Initializable {
    @FXML
    private TextField lognField, finalPriceField, markupField, costPriceField, productNameField, otherExpensesRealizationField, transportationCostField, marketingCostField, distributionCostField,
            otherExpensesProductionField, wagesCostField, overheadCostField, materialCostField, plannedRevenueField;

    @FXML
    private Text clickedLogout, mainPageNavigation;

    @FXML
    private Label labelMessage;

    @FXML
    private Button usernameButton;

    @FXML
    private ContextMenu contextMenu;


    @Getter
    @Setter
    private Product productLabel;
    @Getter
    @Setter
    private RealizationExpenses realizationExpenses;
    @Getter
    @Setter
    private ProductionExpenses productionExpenses;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getProductsData();
        loadContextMenu();
        User user = ClientSocket.getInstance().getUser();

        lognField.setText(productLabel.getCreatedBy().getUsername());
        usernameButton.setText(user.getUsername());
        productNameField.setText(productLabel.getProductName());
        finalPriceField.setText(String.valueOf(productLabel.getFinalPrice()));
        markupField.setText(String.valueOf(productLabel.getMarkup()));
        costPriceField.setText(String.valueOf(productLabel.getCostPrice()));

        plannedRevenueField.setText(String.valueOf(productLabel.getPlannedRevenue()));
        wagesCostField.setText(String.valueOf(productionExpenses.getWagesCost()));
        overheadCostField.setText(String.valueOf(productionExpenses.getOverheadCost()));
        materialCostField.setText(String.valueOf(productionExpenses.getMaterialCost()));
        otherExpensesProductionField.setText(String.valueOf(productionExpenses.getOtherExpenses()));

        transportationCostField.setText(String.valueOf(realizationExpenses.getTransportationCost()));
        marketingCostField.setText(String.valueOf(realizationExpenses.getMarketingCost()));
        distributionCostField.setText(String.valueOf(realizationExpenses.getDistributionCost()));
        otherExpensesRealizationField.setText(String.valueOf(realizationExpenses.getOtherExpenses()));
    }

    private void getProductsData() {
        try {
            Product product = Information.getInstance().getProduct();
            Request request = new Request();
            product.getCreatedBy().setRole(null);
            request.setRequestType(RequestType.GET_INFO);
            request.setRequestMessage(new Gson().toJson(product));
            ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            if(response.getStatus() == ResponseStatus.OK) {
                JsonObject jsonObject = new Gson().fromJson(response.getResponseData(), JsonObject.class);
                productLabel = new Gson().fromJson(jsonObject.get("product"), Product.class);
                realizationExpenses = new Gson().fromJson(jsonObject.get("RealizationExpenses"), RealizationExpenses.class);
                productionExpenses = new Gson().fromJson(jsonObject.get("ProductionExpenses"), ProductionExpenses.class);
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
    public void clickedControlUserNavigation(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/AdminPage.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) mainPageNavigation.getScene().getWindow();
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

    public void clickedButtonBack(ActionEvent actionEvent)  throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/ControlProductPage.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) mainPageNavigation.getScene().getWindow();
        currentStage.close();
    }
}
