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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AdminPageController implements Initializable {

    @FXML
    private Text clickedLogout, controlProductNavigation, mainPageNavigation;

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, Integer> numberColumn;

    @FXML
    private TableColumn<User, String> nameColumn, firstNameColumn, lastNameColumn, emailColumn;

    @FXML
    private TableColumn<Roles, String> roleColumn;

    @FXML
    private TableColumn<User, Void> downloadLogColumn, banColumn, viewColumn, deleteColumn, unBunColumn;

    private ObservableList<User> userList;

    @FXML
    private Label labelMessage;

    @FXML
    private Button usernameButton;

    @FXML
    private ContextMenu contextMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadContextMenu();
        List<User> users = findAllUsers();
        if (users == null || users.isEmpty()) {
            showMessage("Пользователей нет в системе!", "error");
            tableView.setPlaceholder(new Label(""));
        } else {
            userList = FXCollections.observableArrayList(users);
            tableView.setItems(userList);
        }
        bindColumns();
        User user = ClientSocket.getInstance().getUser();
        usernameButton.setText(user.getUsername());
    }

    private void bindColumns() {
        numberColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(userList.indexOf(cellData.getValue()) + 1)
        );

        numberColumn.setStyle("-fx-alignment: CENTER;");
        downloadLogColumn.setStyle("-fx-alignment: CENTER;");
        banColumn.setStyle("-fx-alignment: CENTER;");
        viewColumn.setStyle("-fx-alignment: CENTER;");
        deleteColumn.setStyle("-fx-alignment: CENTER;");
        unBunColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        banColumn.setCellFactory(col -> new TableCell<>() {
            private final Button banButton = new Button("BAN");

            {
                banButton.setOnAction(event -> {
                    User user = getTableRow().getItem();
                    if (user != null) {
                        clickedBunButton(user);
                    }
                });
                banButton.setStyle("-fx-background-color: #ffa500;-fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : banButton);
            }
        });

        unBunColumn.setCellFactory(col -> new TableCell<>() {
            private final Button unBunButton = new Button("UNBAN"); {
                unBunButton.setOnAction(event -> {
                    User user = getTableRow().getItem();
                    if (user != null) {
                        clickedUnBanButton(user);
                    }
                });
                unBunButton.setStyle("-fx-background-color: #66ff00;-fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : unBunButton);
            }
        });

        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteUserAccountButton = new Button("Удалить"); {
                deleteUserAccountButton.setOnAction(event -> {
                    User user = getTableRow().getItem();
                    if (user != null) {
                        clickedDeleteUserButton(user);
                    }
                });
                deleteUserAccountButton.setStyle("-fx-background-color: #ffcccc;-fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteUserAccountButton);
            }
        });

        viewColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("Просмотреть");

            {
                viewButton.setOnAction(event -> {
                    User user = getTableRow().getItem();
                    if (user != null) {
                        clickedViewButton(user);
                    }
                });
                viewButton.setStyle("-fx-background-color: #ffe6cc;-fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewButton);
            }
        });

        downloadLogColumn.setCellFactory(col -> new TableCell<>() {
            private final Button downloadLogButton = new Button("Скачать"); {
                downloadLogButton.setOnAction(event -> {
                    User user = getTableRow().getItem();
                    if (user != null) {
                        clickedDownloadLogButton(user);
                    }
                });
                downloadLogButton.setStyle("-fx-background-color: #ffcccc;-fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : downloadLogButton);
            }
        });
    }

    private void clickedBunButton(User user) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.BAN_USER);
            JsonObject jsonObject = new JsonObject();
            User userAdmin = ClientSocket.getInstance().getUser().clone();
            userAdmin.setRole(null);
            jsonObject.add("user", new Gson().toJsonTree(userAdmin));
            jsonObject.addProperty("user_id", user.getUserId());
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

    private void clickedDownloadLogButton(User user) {
        try {
            Request request = new Request();
            JsonObject jsonObject = new JsonObject();
            User userAdmin = ClientSocket.getInstance().getUser().clone();
            userAdmin.setRole(null);
            jsonObject.add("user", new Gson().toJsonTree(userAdmin));
            jsonObject.addProperty("user_id", user.getUserId());
            request.setRequestMessage(new Gson().toJson(jsonObject));
            request.setRequestType(RequestType.GET_LOG_INFO);
            ClientSocket.getInstance().getOut().println(new Gson().toJsonTree(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            if (response.getStatus() == ResponseStatus.OK) {
                byte[] pdfBytes = new Gson().fromJson(new Gson().toJson(response.getData()), byte[].class);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
                String fileName = "logs_report_" + timestamp + "_user-" + user.getUserId() + ".pdf";
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

    private void clickedUnBanButton(User user) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.UNBAN_USER);
            JsonObject jsonObject = new JsonObject();
            User userAdmin = ClientSocket.getInstance().getUser().clone();
            userAdmin.setRole(null);
            jsonObject.add("user", new Gson().toJsonTree(userAdmin));
            jsonObject.addProperty("user_id", user.getUserId());
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

    private void clickedDeleteUserButton(User user) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.DELETE_ACCOUNT);
            JsonObject jsonObject = new JsonObject();
            User userAdmin = ClientSocket.getInstance().getUser().clone();
            userAdmin.setRole(null);
            jsonObject.add("user", new Gson().toJsonTree(userAdmin));
            jsonObject.addProperty("user_id", user.getUserId());
            request.setRequestMessage(new Gson().toJson(jsonObject));
            ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            if (response.getStatus() == ResponseStatus.OK) {
                showMessage(response.getMessage(), "success");
                List<User> getUsers = new Gson().fromJson(
                        response.getResponseData(), new TypeToken<List<User>>() {}.getType()
                );
                if (getUsers == null || getUsers.isEmpty()) {
                    showMessage("Пользователей нет в системе!", "error");
                    userList.clear();
                    tableView.setPlaceholder(new Label("Нет данных для отображения"));
                } else {
                    userList = FXCollections.observableArrayList(getUsers);
                }
                tableView.setItems(userList);
            } else {
                showMessage(response.getMessage(), "error");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showMessage(e.getMessage(), "error");
        }
        bindColumns();
    }

    private void clickedViewButton(User user) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.GET_USER_INFO);
            JsonObject jsonObject = new JsonObject();
            User userAdmin = ClientSocket.getInstance().getUser().clone();
            userAdmin.setRole(null);
            jsonObject.add("user", new Gson().toJsonTree(userAdmin));
            jsonObject.addProperty("user_id", user.getUserId());
            request.setRequestMessage(new Gson().toJson(jsonObject));
            ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            if (response.getStatus() == ResponseStatus.OK) {
                showMessage(response.getMessage(), "success");
                Information.getInstance().setUser(new Gson().fromJson(response.getResponseData(), User.class));
                redirectToViewPage();
            } else {
                showMessage(response.getMessage(), "error");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showMessage(e.getMessage(), "error");
        }
    }


    private List<User> findAllUsers() {
        try {
            Request request = new Request();
            User user = ClientSocket.getInstance().getUser().clone();
            user.setRole(null);
            request.setRequestType(RequestType.GET_ALL_ACCOUNTS);
            request.setRequestMessage(new Gson().toJson(user));
            ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
            String answer = ClientSocket.getInstance().getIn().readLine();
            Response response = new Gson().fromJson(answer, Response.class);
            if (response.getStatus() == ResponseStatus.OK) {
                showMessage(response.getMessage(), "success");
                return new Gson().fromJson(response.getResponseData(), new TypeToken<List<User>>() {
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/ControlProductPage.fxml"));
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


    public void redirectToViewPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/public/UserInfoPage.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);
            newStage.show();
            Stage currentStage = (Stage) mainPageNavigation.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showMessage(e.getMessage(), "error");
        }
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