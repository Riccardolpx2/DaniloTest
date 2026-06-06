package client.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.model.database.DatabaseManager;
import server.model.database.UtenteDAO;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import server.model.database.entity.UtenteEntity;
import java.time.LocalDate;

import java.io.IOException;

public class ClientRegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private DatePicker birthdate;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    @FXML
    private void initialize(){
        registerButton.disableProperty().bind(
                nameField.textProperty().isEmpty().or(
                surnameField.textProperty().isEmpty()).or(
                birthdate.promptTextProperty().isEmpty()).or(
                usernameField.textProperty().isEmpty()).or(
                passwordField.textProperty().isEmpty()).or(
                birthdate.valueProperty().isNull())
        );
    }


    @FXML
    private void register(){
        // Va instanziata la classe utente che ha (nome, cognome, dataNascita, username, password) e inviata serializzata al server

        String nome = nameField.getText();
        String cognome = surnameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        LocalDate dataNascita = birthdate.getValue();

        UtenteEntity u = new UtenteEntity(username,password,nome,cognome,dataNascita.toString());

        UtenteDAO ud = new UtenteDAO();
        try{
            DatabaseManager.inizializzaDatabase();
            ud.aggiungi(u);
            System.out.println("Utente aggiunto al db");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void backToLogin(ActionEvent event){
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/clientLogin.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
