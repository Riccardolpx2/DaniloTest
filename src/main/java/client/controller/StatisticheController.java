package client.controller;

import client.ClientApp;
import client.network.ConnectionHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import shared.gui.util.SceneManager;
import shared.protocol.DTO.StatDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.IOException;

public class StatisticheController {

    private ConnectionHandler connectionHandler;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label vittorieLabel;

    @FXML
    private Label sconfitteLabel;

    @FXML
    private Label percentualeLabel;

    @FXML
    private Label mediaRispostaLabel;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() throws IOException {
        this.connectionHandler = ClientApp.getInstance().getConnectionHandler();
        this.connectionHandler.setCurrentListener(this::handleMessage);
        connectionHandler.sendMessage(new Message(MessageType.stats, null));
    }

    private void handleMessage(Message message) {
        switch (message.getMsgType()) {
            case statsInfo:
                Platform.runLater(() -> {
                    String username = ClientApp.getInstance().getCurrentUser();
                    StatDTO stat = (StatDTO) message.getPayload();
                    if (stat == null) {
                        stat = new StatDTO(username, 0, 0, 0, 0.0);
                    }
                    setStatistiche(username, stat);
                });
                break;
            case generalError:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore ottenimento Statistiche");
                    alert.setHeaderText(null);
                    alert.setContentText("Impossibile caricare le statistiche.");
                    alert.showAndWait();
                });
                break;
        }
    }

    /**
     * Metodo per caricare le statistiche di un utente specifico e aggiornare la UI.
     *
     * @param username   l'username del giocatore
     * @param statistica l'oggetto contenente le statistiche del giocatore
     */
    public void setStatistiche(String username, StatDTO statistica) {
        if (username != null) {
            usernameLabel.setText("Statistiche per " + username);
        }

        if (statistica != null) {
            vittorieLabel.setText(String.valueOf(statistica.getVittorie()));
            sconfitteLabel.setText(String.valueOf(statistica.getSconfitte()));
            percentualeLabel.setText(statistica.getPercentualeVittorie() + "%");
            mediaRispostaLabel.setText(String.format("%.1f s", statistica.getMediaRisposta()));
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        SceneManager.switchScene(event, "/fxml/client/clientDashboard.fxml");
    }
}
