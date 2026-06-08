package server.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import server.model.service.AuthService;

public class ServerLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private AuthService authService;

    @FXML
    private void initialize(){


        loginButton.disableProperty().bind(
                usernameField.textProperty().isEmpty().or(
                        passwordField.textProperty().isEmpty()
                ));
    }


    @FXML
    private void login(ActionEvent event){
        String username = usernameField.getText();
        String password = passwordField.getText();



        // Da completare con il metodo per inviare al server
    }

//    @FXML
//    private void register(ActionEvent event){
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/clientRegister.fxml"));
//            Parent root = loader.load();
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }
}
