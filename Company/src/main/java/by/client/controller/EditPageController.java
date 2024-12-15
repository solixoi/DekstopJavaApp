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
import by.client.utility.Information;
import by.client.utility.ValidationUtils;
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
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class EditPageController implements Initializable {

    @FXML
    private TextField productNameField, otherExpensesRealizationField, transportationCostField, marketingCostField, distributionCostField,
            otherExpensesProductionField, wagesCostField, overheadCostField, materialCostField, plannedRevenueField;

    @FXML
    private Text clickedLogout, mainPageNavigation, pricesPageNavigation, adminPageNavigation, reportPageNavigation;

    @FXML
    private Button buttonUpdate;

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
        if (user.getRole().equals(Roles.ADMIN)) {
            adminPageNavigation.setVisible(true);
            reportPageNavigation.setVisible(true);
        } else if (user.getRole().equals(Roles.MANAGER)) {
            reportPageNavigation.setVisible(true);
        }
        usernameButton.setText(user.getUsername());
        productNameField.setText(productLabel.getProductName());
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

    public void clickedButtonUpdate (ActionEvent actionEvent) throws IOException {
        Request request = new Request();

        if (!ValidationUtils.validateProductName(productNameField.getText())) {
            showMessage("Ошибка валидации: Некорректное название изделия от 2 до 30 символов", "error");
            return;
        }

        if (!ValidationUtils.validateBigDecimalField(plannedRevenueField.getText())) {
            showMessage("Ошибка валидации: Некорректное значение ожидаемой выручки", "error");
            return;
        }

        if (!ValidationUtils.validateBigDecimalField(wagesCostField.getText())) {
            showMessage("Ошибка валидации: Некорректное значение расходов на заработную плату", "error");
            return;
        }

        if (!ValidationUtils.validateBigDecimalField(materialCostField.getText())) {
            showMessage("Ошибка валидации: Некорректное значение расходов на материалы", "error");
            return;
        }

        if (!ValidationUtils.validateBigDecimalField(overheadCostField.getText())) {
            showMessage("Ошибка валидации: Некорректное значение накладных расходов", "error");
            return;
        }

        if (!ValidationUtils.validateBigDecimalField(otherExpensesProductionField.getText())) {
            showMessage("Ошибка валидации: Некорректное значение прочих расходов на производство", "error");
            return;
        }

        if (!ValidationUtils.validateBigDecimalField(marketingCostField.getText())) {
            showMessage("Ошибка валидации: Некорректное значение расходов на маркетинг", "error");
            return;
        }

        if (!ValidationUtils.validateBigDecimalField(distributionCostField.getText())) {
            showMessage("Ошибка валидации: Некорректное значение расходов на распределение", "error");
            return;
        }

        if (!ValidationUtils.validateBigDecimalField(transportationCostField.getText())) {
            showMessage("Ошибка валидации: Некорректное значение расходов на транспортировку", "error");
            return;
        }

        if (!ValidationUtils.validateBigDecimalField(otherExpensesRealizationField.getText())) {
            showMessage("Ошибка валидации: Некорректное значение прочих расходов на реализацию", "error");
            return;
        }


        JsonObject jsonObject = new JsonObject();
        User user = ClientSocket.getInstance().getUser().clone();
        user.setRole(null);

        jsonObject.add("user", new Gson().toJsonTree(user));

        Product sendProduct = new Product(
                productLabel.getProductId(),
                user,
                productNameField.getText(),
                new BigDecimal(plannedRevenueField.getText())
        );
        jsonObject.add("product", new Gson().toJsonTree(sendProduct));

        ProductionExpenses productionExpenses = new ProductionExpenses(
                new BigDecimal(wagesCostField.getText()),
                new BigDecimal(materialCostField.getText()),
                new BigDecimal(overheadCostField.getText()),
                new BigDecimal(otherExpensesProductionField.getText())
        );
        jsonObject.add("ProductionExpenses", new Gson().toJsonTree(productionExpenses));

        RealizationExpenses realizationExpenses = new RealizationExpenses(
                new BigDecimal(marketingCostField.getText()),
                new BigDecimal(distributionCostField.getText()),
                new BigDecimal(transportationCostField.getText()),
                new BigDecimal(otherExpensesRealizationField.getText())
        );
        jsonObject.add("RealizationExpenses", new Gson().toJsonTree(realizationExpenses));


        request.setRequestMessage(new Gson().toJson(jsonObject));
        request.setRequestType(RequestType.UPDATE_USER_PRODUCT);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        String answer = ClientSocket.getInstance().getIn().readLine();
        Response response = new Gson().fromJson(answer, Response.class);
        if (response.getStatus() == ResponseStatus.OK) {
            showMessage(response.getMessage(), "success");
        } else {
            showMessage(response.getMessage(), "error");
        }
    }

    public void clickedButtonBack(ActionEvent actionEvent)  throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/AllPricesPageNavigation.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setMaximized(true);
        newStage.show();
        Stage currentStage = (Stage) reportPageNavigation.getScene().getWindow();
        currentStage.close();
    }
}
