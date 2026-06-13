package client.controller;

import client.ClientApp;
import client.network.ConnectionHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.gui.util.SceneManager;
import shared.protocol.DTO.GameSearchDTO;
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
    private ComboBox<String> difficultyComboBox;




    @FXML
    private void initialize(){
        if (difficultyComboBox != null) {
            difficultyComboBox.getItems().addAll("FACILE", "MEDIA", "DIFFICILE");
            difficultyComboBox.getSelectionModel().select("MEDIA");
        }

        String username = ClientApp.getInstance().getCurrentUser();
        if(username != null && !username.isEmpty()){
            welcomeLabel.setText("Ciao " + username + "!");
        }

        this.connectionHandler  = ClientApp.getInstance().getConnectionHandler();
        this.connectionHandler.setCurrentListener(this::handleMessage);
    }
    
    private void handleMessage(Message message){
        switch(message.getMsgType()){
            case GAME_START:
                Platform.runLater(() -> {
                    waitingOverlay.setVisible(false);
                    mainContent.setDisable(false);
                    GameStartDTO gs = (GameStartDTO) message.getPayload();


                    try {
                        Stage stage = (Stage) waitingOverlay.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/clientGame.fxml"));
                        Parent root = loader.load();

                        GameController gameController = loader.getController();
                        gameController.inizializzaDati(gs.getSfindateUsername());

                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
                
            case GAME_SEARCH_ERROR:
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
            connectionHandler.sendMessage(new Message(MessageType.GAME_SEARCH_REQUEST,new GameSearchDTO(difficultyComboBox.getSelectionModel().getSelectedItem())));
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
            connectionHandler.sendMessage(new Message(MessageType.GAME_SEARCH_CANCEL, null));
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
            ClientApp.getInstance().getConnectionHandler().sendMessage(new Message(MessageType.LOGOUT_REQUEST, null));
            ClientApp.getInstance().setCurrentUser(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Logout effettuato. Ritorno alla schermata di Login.");

        SceneManager.switchScene(event, "/fxml/client/clientLogin.fxml");
    }
}
