package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.protocol.DTO.LoginDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.*;
import java.net.Socket;

public class ClientLoginController {

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
    private void login(ActionEvent event) throws IOException, ClassNotFoundException {
        String username = usernameField.getText();
        String password = passwordField.getText();


        // Scrivo giusto per fare un test
        Socket socket = new Socket("127.0.0.1", 5000);
        ObjectOutputStream OOS = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        OOS.flush();
        ObjectInputStream OIS = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));


        OOS.writeObject(new Message(MessageType.login, new LoginDTO(username, password)));
        OOS.flush();
        Message m = (Message) OIS.readObject();
        System.out.println(m.getMsgType());
        // *********************************
    }

    @FXML
    private void register(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/clientRegister.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
