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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import shared.protocol.DTO.DomandaDTO;
import shared.protocol.DTO.RispostaGiocatoreDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameController {

    private ConnectionHandler connectionHandler;

    @FXML
    private Label timerLabel;

    @FXML
    private TextFlow textFlow;

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
                Task<Void> task = new Task<Void>(){
                    @Override
                    protected Void call(){
                        Platform.runLater(() -> {                           
                            DomandaDTO domanda = (DomandaDTO) message.getPayload();
                            updateTextFlow(domanda.getTestoCifrato(), domanda.getParoleCifrate());
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

    private void updateTextFlow(String text, List<String> paroleCifrate){

        textFlow.getChildren().clear();
        
        if (paroleCifrate == null || paroleCifrate.isEmpty()) {
            textFlow.getChildren().add(new Text(text));
            return;
        }
        
        String regex = "(?i)\\b(" + String.join("|", paroleCifrate) + ")\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        
        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                textFlow.getChildren().add(new Text(text.substring(lastEnd, matcher.start())));
            }
            Text redText = new Text(matcher.group());
            redText.setStyle("-fx-fill: red; -fx-font-weight: bold;");
            textFlow.getChildren().add(redText);
            lastEnd = matcher.end();
        }
        if (lastEnd < text.length()) {
            textFlow.getChildren().add(new Text(text.substring(lastEnd)));
        }
    }

    @FXML
    void submitAnswer(ActionEvent event) throws IOException {
        String answer = answerField.getText().trim();
        if (answer.isEmpty()) {
            statusLabel.setText("Inserisci la risposta");
            statusLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        // Qui inviare la risposta al server tramite socket
        this.connectionHandler.sendMessage(new Message(MessageType.gameAnswer, new RispostaGiocatoreDTO(answer)));

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
