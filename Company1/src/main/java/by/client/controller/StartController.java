package by.client.controller;

import by.client.utility.ClientSocket;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class StartController {
    public StartController(Stage primaryStage) throws IOException {
        ClientSocket.getInstance().getSocket();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/public/Login.fxml")));
        primaryStage.setTitle("Condorcet");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
