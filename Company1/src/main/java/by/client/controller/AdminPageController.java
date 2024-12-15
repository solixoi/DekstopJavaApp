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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AdminPageController implements Initializable {

    @FXML
    private Text clickedLogout, controlProductNavigation, mainPageNavigation;

    @FXML
    private TableView<Product> tableView;

    @FXML
    private TableColumn<Product, Integer> numberColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, BigDecimal> revenueColumn, costColumn, markupColumn, priceColumn;

    @FXML
    private TableColumn<Product, Void> marginalityRepColumn, expensesProductRepColumn, viewColumn;

    private ObservableList<Product> productList;

    @FXML
    private Label labelMessage;

    @FXML
    private Button usernameButton;

    @FXML
    private ContextMenu contextMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        loadContextMenu();
//        List<Product> products = findAllProducts();
//        if (products == null || products.isEmpty()) {
//            showMessage("У вас нет расчетов!", "error");
//            tableView.setPlaceholder(new Label(""));
//        } else {
//            productList = FXCollections.observableArrayList(products);
//            tableView.setItems(productList);
//        }
//        bindColumns();
//        User user = ClientSocket.getInstance().getUser();
//        usernameButton.setText(user.getUsername());
    }

    private void bindColumns() {
        numberColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(productList.indexOf(cellData.getValue()) + 1)
        );

        numberColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("plannedRevenue"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        markupColumn.setCellValueFactory(new PropertyValueFactory<>("markup"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("finalPrice"));

        revenueColumn.setCellFactory(CustomTableCellFactory.forTableColumn(" руб."));
        costColumn.setCellFactory(CustomTableCellFactory.forTableColumn(" руб."));
        markupColumn.setCellFactory(CustomTableCellFactory.forTableColumn("%"));
        priceColumn.setCellFactory(CustomTableCellFactory.forTableColumn(" руб."));

        marginalityRepColumn.setCellFactory(col -> new TableCell<>() {
            private final Button marginalityRepButton = new Button("Скачать отчет");

            {
                marginalityRepButton.setOnAction(event -> {
                    Product product = getTableRow().getItem();
                    if (product != null) {
                        clickedMarginalityRepButton(product);
                    }
                });
                marginalityRepButton.setStyle("-fx-background-color: #ffe6cc;-fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : marginalityRepButton);
            }
        });

        expensesProductRepColumn.setCellFactory(col -> new TableCell<>() {
            private final Button expensesProductRepButton = new Button("Скачать отчет");

            {
                expensesProductRepButton.setOnAction(event -> {
                    Product product = getTableRow().getItem();
                    if (product != null) {
                        clickedExpensesProductRepButton(product);
                    }
                });
                expensesProductRepButton.setStyle("-fx-background-color: #ffcccc;-fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : expensesProductRepButton);
            }
        });

        viewColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("Просмотреть");

            {
                viewButton.setOnAction(event -> {
                    Product product = getTableRow().getItem();
                    if (product != null) {
                        clickedViewButton(product);
                    }
                });
                viewButton.setStyle("-fx-background-color: #e6f7ff;-fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewButton);
            }
        });
    }

    private void clickedMarginalityRepButton(Product product) {
        try {
            Request request = new Request();
            JsonObject jsonObject = new JsonObject();
            User user = ClientSocket.getInstance().getUser().clone();
            user.setRole(null);
            jsonObject.add("user", new Gson().toJsonTree(user));
            jsonObject.add("product_id", new Gson().toJsonTree(product.getProductId()));
            request.setRequestMessage(new Gson().toJson(jsonObject));
            request.setRequestType(RequestType.CREATE_REPORT_MARGINALITY);
            ClientSocket.getInstance().getOut().println(new Gson().toJsonTree(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            if (response.getStatus() == ResponseStatus.OK) {
                byte[] pdfBytes = new Gson().fromJson(new Gson().toJson(response.getData()), byte[].class);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
                String fileName = "marginality_report_" + timestamp + "_product-" + product.getProductId() + ".pdf";
                Path filePath = Paths.get("files", fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, pdfBytes);
                showMessage("Файл сохранен: " + filePath.toAbsolutePath(), "success");
            } else {
                showMessage("Ошибка: " + response.getMessage(), "error");
            }
        } catch (Exception e) {
            showMessage("Произошла ошибка: " + e.getMessage(), "error");
        }
    }

    private void clickedExpensesProductRepButton(Product product) {
        try {
            Request request = new Request();
            JsonObject jsonObject = new JsonObject();
            User user = ClientSocket.getInstance().getUser().clone();
            user.setRole(null);
            jsonObject.add("user", new Gson().toJsonTree(user));
            jsonObject.add("product_id", new Gson().toJsonTree(product.getProductId()));
            request.setRequestMessage(new Gson().toJson(jsonObject));
            request.setRequestType(RequestType.CREATE_REPORT_EXPENSES_PRODUCT);
            ClientSocket.getInstance().getOut().println(new Gson().toJsonTree(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            if (response.getStatus() == ResponseStatus.OK) {
                byte[] pdfBytes = new Gson().fromJson(new Gson().toJson(response.getData()), byte[].class);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
                String fileName = "expenses_report_" + timestamp + "_product-" + product.getProductId() + ".pdf";
                Path filePath = Paths.get("files", fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, pdfBytes);
                showMessage("Файл сохранен: " + filePath.toAbsolutePath(), "success");
            } else {
                showMessage("Ошибка: " + response.getMessage(), "error");
            }
        } catch (Exception e) {
            showMessage("Произошла ошибка: " + e.getMessage(), "error");
        }
    }

    private void clickedViewButton(Product product) {
        Information.getInstance().setProduct(product);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/ViewManagerPage.fxml"));
        try {
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);
            newStage.show();
            Stage currentStage = (Stage) controlProductNavigation.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<Product> findAllProducts() {
        try {
            Request request = new Request();
            User user = ClientSocket.getInstance().getUser().clone();
            user.setRole(null);
            request.setRequestType(RequestType.GET_ALL_PRODUCTS);
            request.setRequestMessage(new Gson().toJson(user));
            ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            if (response.getStatus() == ResponseStatus.OK) {
                showMessage(response.getMessage(), "success");
                return new Gson().fromJson(response.getResponseData(), new TypeToken<List<Product>>() {
                }.getType());
            } else {
                showMessage(response.getMessage(), "error");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showMessage(e.getMessage(), "error");
        }
        return null;
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
            Stage currentStage = (Stage) controlProductNavigation.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
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