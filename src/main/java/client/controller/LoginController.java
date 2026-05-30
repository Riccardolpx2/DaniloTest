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
//        try {
//            // 1. Carica il file FXML della schermata di registrazione
//            // NOTA: Inserisci il percorso corretto del tuo file. Se è nella stessa cartella, basta il nome.
//            Parent root = FXMLLoader.load(getClass().getResource("/reregistrazione.fxml"));
//
//            // 2. Recupera la finestra (Stage) corrente partendo dal bottone che è stato cliccato
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//            // 3. Crea una nuova scena con il contenuto appena caricato
//            Scene scene = new Scene(root);
//
//            // 4. Cambia la scena sul palcoscenico e mostrala
//            stage.setScene(scene);
//            stage.show();
//
//        } catch (IOException e) {
//            System.err.println("Errore durante il caricamento della schermata di registrazione:");
//            e.printStackTrace();
//       }
    }
}
