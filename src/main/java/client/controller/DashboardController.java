package client.controller;

import client.ClientApp;
import client.network.ConnectionHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.gui.util.SceneManager;
import shared.protocol.DTO.GameStartDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.IOException;

public class DashboardController {

    private ConnectionHandler connectionHandler;

    @FXML
    private BorderPane mainContent;
    @FXML
    private VBox waitingOverlay;

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

        this.connectionHandler  = ClientApp.getInstance().getConnectionHandler();
        this.connectionHandler.setCurrentListener(this::handleMessage);
    }
    
    private void handleMessage(Message message){
        switch(message.getMsgType()){
            case gameStart:
                Platform.runLater(() -> {
                    waitingOverlay.setVisible(false);
                    mainContent.setDisable(false);
                    GameStartDTO gs = (GameStartDTO) message.getPayload();
                    gs.getSfindateUsername();


                    Stage stage = (Stage) waitingOverlay.getScene().getWindow();
                    SceneManager.switchScene(stage, "/fxml/client/clientGame.fxml");
                });
                break;
                
            case gameSearchError:
                Platform.runLater(() -> {
                    waitingOverlay.setVisible(false);
                    mainContent.setDisable(false);
                    
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore Ricerca Partita");
                    alert.setHeaderText(null);
                    alert.setContentText("Impossibile trovare una partita al momento. Riprova tra poco.");
                    alert.showAndWait();
                });
                break;
                
            default:
                System.out.println("Messaggio non gestito dal Dashboard: " + message.getMsgType());
        }
    }

    @FXML
    private void iniziaPartita(ActionEvent event){
        System.out.println("Ricerca partita avviata. In attesa del server...");
        
        // Mostriamo l'overlay di caricamento
        waitingOverlay.setVisible(true);
        mainContent.setDisable(true); // Impedisce i click sui bottoni sottostanti

        try {
            connectionHandler.sendMessage(new Message(MessageType.gameSearch,null));
        } catch (IOException e) {
            e.printStackTrace();
            waitingOverlay.setVisible(false);
            mainContent.setDisable(false);
        }
    }

    @FXML
    private void annullaRicerca(ActionEvent event){
        System.out.println("Annullamento ricerca partita in corso...");
        
        // Nascondiamo l'overlay e riabilitiamo la dashboard
        waitingOverlay.setVisible(false);
        mainContent.setDisable(false);

        try {
            connectionHandler.sendMessage(new Message(MessageType.gameSearchCancel, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void viewStatistiche(ActionEvent event){

        SceneManager.switchScene(event, "/fxml/client/clientStatistiche.fxml");


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
