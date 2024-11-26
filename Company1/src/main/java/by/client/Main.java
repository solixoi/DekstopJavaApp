package by.client;

import by.client.controller.StartController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        StartController startController = new StartController(primaryStage);
    }

    public static void main(String[] args) {
        launch();
    }
}