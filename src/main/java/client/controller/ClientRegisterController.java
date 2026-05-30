package client.controller;

import server.model.database.DatabaseManager;
import server.model.database.UtenteDAO;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import shared.model.Utente;

import java.awt.event.ActionEvent;

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
        java.time.LocalDate dataNascita = birthdate.getValue();

        Utente u = new Utente(username,password,nome,cognome,dataNascita.toString());

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
    private void backToLogin(){

    }
}
