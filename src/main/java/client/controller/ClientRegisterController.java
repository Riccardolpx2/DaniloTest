package client.controller;

import client.ClientApp;
import client.network.ConnectionHandler;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.fxml.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.time.LocalDate;

import shared.gui.util.SceneManager;
import shared.protocol.DTO.RegisterDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.IOException;

public class ClientRegisterController {

    private ConnectionHandler connectionHandler;

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

        this.connectionHandler = ClientApp.getInstance().getConnectionHandler();
        this.connectionHandler.setCurrentListener(this::handleMessage);
    }

    private void handleMessage(Message message){
        switch(message.getMsgType()){
            case registerSuccess:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Registrazione Completata");
                    alert.setHeaderText(null);
                    alert.setContentText("La registrazione è avvenuta con successo! Clicca 'ok' per toranre al login.");
                    alert.showAndWait();


                    Stage stage = (Stage) backButton.getScene().getWindow();
                    SceneManager.switchScene(stage, "/fxml/client/clientLogin.fxml");
                });
                break;
            case registerFailure:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore di Registrazione");
                    alert.setHeaderText(null);
                    alert.setContentText("Impossibile completare la registrazione. È possibile che l'username sia già in uso. Riprova.");
                    alert.showAndWait();
                });
                break;
        }
    }


    @FXML
    private void register() throws IOException {
        String nome = nameField.getText();
        String cognome = surnameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        LocalDate dataNascita = birthdate.getValue();

        RegisterDTO registerPayload = new RegisterDTO(username, password, nome, cognome, dataNascita.toString());
        Message msg = new Message(MessageType.register, registerPayload);


        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws IOException {
                connectionHandler.sendMessage(msg);
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    @FXML
    private void backToLogin(ActionEvent event){
        SceneManager.switchScene(event, "/fxml/client/clientLogin.fxml");
    }
}
