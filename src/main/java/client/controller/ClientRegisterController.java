package client.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import server.model.database.entity.UtenteEntity;
import java.time.LocalDate;

import shared.gui.util.SceneManager;
import shared.protocol.DTO.RegisterDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

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

        RegisterDTO registerPayload = new RegisterDTO(username, password, nome, cognome, dataNascita.toString());
        Message msg = new Message(MessageType.register, username, registerPayload);
        
        // TODO: Invia msg tramite la classe che gestisce il Socket lato client
        System.out.println("Pronto per l'invio del messaggio di tipo: " + msg.getMsgType());
    }

    @FXML
    private void backToLogin(ActionEvent event){
        SceneManager.switchScene(event, "/fxml/clientLogin.fxml");
    }
}
