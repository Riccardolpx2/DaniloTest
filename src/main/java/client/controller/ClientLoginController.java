package client.controller;

import client.ClientApp;
import client.network.ConnectionHandler;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.gui.util.SceneManager;
import shared.protocol.DTO.LoginDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.*;

public class ClientLoginController {

    private ConnectionHandler connectionHandler;

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

        this.connectionHandler = ClientApp.getInstance().getConnectionHandler();
        this.connectionHandler.setCurrentListener(this::handleMessage);
    }


    private void handleMessage(Message message) {
        switch (message.getMsgType()) {
            case LOGIN_SUCCESS:
                Platform.runLater(() -> {
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    SceneManager.switchScene(stage, "/fxml/client/clientDashboard.fxml");
                });
                break;
            case LOGIN_FAILURE:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login fallito");
                    alert.setHeaderText(null);
                    alert.setContentText((String) message.getPayload());
                    alert.showAndWait();
                });
                break;
            default:
                System.out.println("Messaggio non gestito dal Login: " + message.getMsgType());
                break;
        }
    }


    @FXML
    private void login(ActionEvent event) throws IOException, ClassNotFoundException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        ClientApp.getInstance().setCurrentUser(username);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception{
                connectionHandler.sendMessage(new Message(MessageType.LOGIN_REQUEST, new LoginDTO(username, password)));
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void register(ActionEvent event){
        SceneManager.switchScene(event, "/fxml/client/clientRegister.fxml");
    }
}
