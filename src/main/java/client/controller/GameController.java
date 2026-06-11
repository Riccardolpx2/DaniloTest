package client.controller;

import client.ClientApp;
import client.network.ConnectionHandler;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import shared.protocol.DTO.DomandaDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.IOException;

public class GameController {

    private ConnectionHandler connectionHandler;

    @FXML
    private Label timerLabel;

    @FXML
    private Label textLabel;

    @FXML
    private TextField answerField;

    @FXML
    private Button submitButton;

    @FXML
    private Label statusLabel;
    
    @FXML
    private Label sfidanteLabel;

    private int timeRemaining = 30;
    private Thread timerThread;
    private String usernameSfidante;



    /*
    metodo per scambiare info sfidante dal dashbaoard
     */
    public void inizializzaDati(String messaggio) {
        usernameSfidante=messaggio;
        Platform.runLater(() -> {
            if (sfidanteLabel != null) {
                sfidanteLabel.setText("Partita contro " + usernameSfidante);
            }
        });
    }

    @FXML
    public void initialize() {
        this.connectionHandler = ClientApp.getInstance().getConnectionHandler();
        this.connectionHandler.setCurrentListener(this::messageHandler);
    }

    private void messageHandler(Message message){
        switch(message.getMsgType()){
            case gameQuestion:
                startTimer();
                Task<Void> task = new Task(){
                    @Override
                    protected Void call(){
                        Platform.runLater(() -> {
                            updateTextLabel(((DomandaDTO) message.getPayload()).getTestoCifrato());
                        });
                        return null;
                    }
                };
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
                break;
            default:
                // TODO: gli altri  case
        }
    }

    private void startTimer() {
        Task<Void> timerTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (timeRemaining > 0 && !isCancelled()) {
                    Platform.runLater(() -> updateTimerLabel(timeRemaining));
                    Thread.sleep(1000);
                    timeRemaining--;
                }
                if (timeRemaining <= 0) {
                    Platform.runLater(() -> {
                        updateTimerLabel(0);
                        timeUp();
                    });
                }
                return null;
            }
        };

        timerThread = new Thread(timerTask);
        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void updateTimerLabel(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, secs));
    }

    private void updateTextLabel(String text){
        textLabel.setText(text);
    }

    @FXML
    void submitAnswer(ActionEvent event) throws IOException {
        String answer = answerField.getText().trim();
        if (answer.isEmpty()) {
            statusLabel.setText("Inserisci una parola.");
            statusLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        // Qui inviare la risposta al server tramite socket
        this.connectionHandler.sendMessage(new Message(MessageType.gameAnswer, answer));

        System.out.println("Risposta inviata: " + answer);
        statusLabel.setText("Risposta inviata in attesa di verifica...");
        statusLabel.setStyle("-fx-text-fill: lightgreen;");
        
        answerField.setDisable(true);
        submitButton.setDisable(true);
    }

    private void timeUp() {
        statusLabel.setText("Tempo scaduto!");
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        answerField.setDisable(true);
        submitButton.setDisable(true);
    }
}
