package client.controller;

import client.ClientApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import shared.gui.util.SceneManager;
import shared.protocol.Message;
import shared.protocol.MessageType;

public class DashboardController {

    @FXML
    private Button playButton;
    @FXML
    private Button statsButton;
    @FXML
    private Button logoutButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    private ListView<String> listadash;


    @FXML
    private void initialize(){
        String username = ClientApp.getInstance().getCurrentUser();
        if(username != null && !username.isEmpty()){
            welcomeLabel.setText("Ciao " + username + "!");
        }
    }

    @FXML
    private void iniziaPartita(ActionEvent event){
        System.out.println("la partita è iniziata");
        SceneManager.switchScene(event, "/fxml/client/clientGame.fxml");
    }


    @FXML
    private void viewStatistiche(){
        // Da fare
    }

    @FXML
    private void logout(ActionEvent event){
        try {
            ClientApp.getInstance().getConnectionHandler().sendMessage(new Message(MessageType.logout, null));
            ClientApp.getInstance().setCurrentUser(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Logout effettuato. Ritorno alla schermata di Login.");

        SceneManager.switchScene(event, "/fxml/client/clientLogin.fxml");
    }
}
