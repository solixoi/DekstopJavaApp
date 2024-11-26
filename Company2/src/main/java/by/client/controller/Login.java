package by.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Login {

    @FXML
    private TextField textfieldLogin;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        // Инициализация, если необходимо
    }

    @FXML
    public void onLoginButtonClicked() {
        String login = textfieldLogin.getText();
        String password = passwordField.getText();
        System.out.println("Логин: " + login);
        System.out.println("Пароль: " + password);
    }
}
