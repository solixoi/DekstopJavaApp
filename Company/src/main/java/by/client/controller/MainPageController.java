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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import by.client.models.entities.User;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private Menu adminMenu;

    @FXML
    private Menu managerMenu;

    @FXML
    private Menu userMenu;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField productNameField, plannedRevenueField, wagesCostField, materialCostField,
            overheadCostField, otherExpensesProductionField, marketingCostField, distributionCostField,
            transportationCostField, otherExpensesRealizationField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = ClientSocket.getInstance().getUser();
        if (user.getRole().equals(Roles.ADMIN)) {
            adminMenu.setVisible(true);
            managerMenu.setVisible(true);
        } else if (user.getRole().equals(Roles.MANAGER)) {
            managerMenu.setVisible(true);
        }
        usernameLabel.setText(user.getUsername());
    }

    @FXML
    public void calculateCost() {
    }

    @FXML
    public void viewAllItems() {
    }

    @FXML
    public void adminFunctionality() {
    }

    @FXML
    public void viewProfile() {
    }

    @FXML
    public void managerFunctionality() {
        // Handle manager functionality action
    }

    @FXML
    public void logout() {
        ClientSocket.getInstance().setUser(null);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/Login.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);
            newStage.show();
            Stage currentStage = (Stage) usernameLabel.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    public void submitExpenses() throws IOException {
        Request request = new Request();

        JsonObject jsonObject = new JsonObject();
        User user = ClientSocket.getInstance().getUser();
        user.setRole(null);
        jsonObject.add("user", new Gson().toJsonTree(user));

        Product sendProduct = new Product(36L, productNameField.getText(), BigDecimal.valueOf(Long.parseLong(plannedRevenueField.getText())));
        jsonObject.add("product", new Gson().toJsonTree(sendProduct));

        ProductionExpenses productionExpenses = new ProductionExpenses( BigDecimal.valueOf(Long.parseLong(wagesCostField.getText())), BigDecimal.valueOf(Long.parseLong(materialCostField.getText())),
                BigDecimal.valueOf(Long.parseLong(overheadCostField.getText())),  BigDecimal.valueOf(Long.parseLong(otherExpensesProductionField.getText())));
        jsonObject.add("ProductionExpenses", new Gson().toJsonTree(productionExpenses));


        RealizationExpenses realizationExpenses = new RealizationExpenses( BigDecimal.valueOf(Long.parseLong(marketingCostField.getText())), BigDecimal.valueOf(Long.parseLong(distributionCostField.getText())),
                BigDecimal.valueOf(Long.parseLong(transportationCostField.getText())),  BigDecimal.valueOf(Long.parseLong(otherExpensesRealizationField.getText())));
        jsonObject.add("RealizationExpenses", new Gson().toJsonTree(realizationExpenses));

        request.setRequestMessage(new Gson().toJson(jsonObject));
        request.setRequestType(RequestType.UPDATE_USER_PRODUCT);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        String answer = ClientSocket.getInstance().getIn().readLine();
        Response response = new Gson().fromJson(answer, Response.class);
        if (response.getStatus() == ResponseStatus.OK) {
            System.out.println("Successfully registered!");
        } else {
            System.out.println(response.getMessage() + "error");
        }
    }

    @FXML
    public void showUserMenu(MouseEvent event) {
        userMenu.show();
    }

    @FXML
    public void hideUserMenu(MouseEvent event) {
        userMenu.hide();
    }

}
