package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private void initialize(){
        loginButton.disableProperty().bind(
                usernameField.textProperty().isEmpty().or(
                        passwordField.textProperty().isEmpty()
                ));
    }


    @FXML
    private void login(){
        String username = usernameField.getText();
        String password = passwordField.getText();

    }

    @FXML
    private void register(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
    }
}
