package client.controller;

import client.ClientApp;
import client.network.ConnectionHandler;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import shared.protocol.DTO.DomandaDTO;
import shared.protocol.DTO.EsitoRoundDTO;
import shared.protocol.DTO.RispostaGiocatoreDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;
import shared.gui.util.SceneManager;

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

    // Elementi dell'Overlay
    @FXML
    private VBox resultOverlay;

    @FXML
    private Label roundResultLabel;

    @FXML
    private Label correctWordLabel;

    @FXML
    private Label myScoreLabel;

    @FXML
    private Label opponentScoreLabel;

    private int timeRemaining = 30;
    private Thread timerThread;
    private String usernameSfidante;

    public void inizializzaDati(String messaggio) {
        usernameSfidante = messaggio;
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
                Platform.runLater(() -> {
                    if (resultOverlay != null) {
                        resultOverlay.setVisible(false);
                    }
                    answerField.setDisable(false);
                    submitButton.setDisable(false);
                    answerField.clear();
                    statusLabel.setText("");

                    DomandaDTO domanda = (DomandaDTO) message.getPayload();
                    updateTextFlow(domanda.getTestoCifrato(), domanda.getParoleCifrate());

                    if (timerThread != null && timerThread.isAlive()) {
                        timerThread.interrupt();
                    }
                    timeRemaining = 30;
                    startTimer();
                });
                break;
            case gameResponse:
                Platform.runLater(() -> {
                    if (timerThread != null) {
                        timerThread.interrupt();
                    }
                    EsitoRoundDTO esito = (EsitoRoundDTO) message.getPayload();
                    showRoundResult(esito);
                });
                break;
            case gameEnd:
                Platform.runLater(() -> {
                    if (timerThread != null) {
                        timerThread.interrupt();
                    }
                    showGameEnd((String) message.getPayload());
                });
                break;
            default:
                System.out.println("Messaggio non gestito dal GameController: " + message.getMsgType());
        }
    }

    private void showRoundResult(EsitoRoundDTO esito) {
        answerField.setDisable(true);
        submitButton.setDisable(true);

        String currentUser = ClientApp.getInstance().getCurrentUser();

        // 1. Esito del round
        if (roundResultLabel != null) {
            if (esito.getUsernameVincitore() == null) {
                roundResultLabel.setText("Nessun vincitore in questo round!");
                roundResultLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 32px; -fx-font-weight: bold;");
            } else if (esito.getUsernameVincitore().equals(currentUser)) {
                roundResultLabel.setText("Hai vinto il round!");
                roundResultLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 38px; -fx-font-weight: bold;");
            } else {
                roundResultLabel.setText("Ha vinto " + esito.getUsernameVincitore() + "!");
                roundResultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 38px; -fx-font-weight: bold;");
            }
        }

        // 2. Parola corretta
        if (correctWordLabel != null) {
            correctWordLabel.setText("La parola corretta era: " + esito.getParolaSoluzione());
        }

        // TODO modificare G1 e G2 per avere un metodo che invia il punteggio passandogli l'utente
        int mioPunteggio = esito.getPunteggioAttualeG1();
        int punteggioAvversario = esito.getPunteggioAttualeG2();

        if (myScoreLabel != null) {
            myScoreLabel.setText(String.valueOf(mioPunteggio));
        }
        if (opponentScoreLabel != null) {
            opponentScoreLabel.setText(String.valueOf(punteggioAvversario));
        }

        // 4. Mostra l'overlay per 5 secondi
        if (resultOverlay != null) {
            resultOverlay.setVisible(true);
            resultOverlay.toFront();

            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(e -> resultOverlay.setVisible(false));
            pause.play();
        }
    }

    private void showGameEnd(String message) {
        if (resultOverlay != null) {
            resultOverlay.setVisible(false);
        }
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: gold;");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partita Terminata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        if (textFlow.getScene() != null && textFlow.getScene().getWindow() != null) {
            SceneManager.switchScene((javafx.stage.Stage) textFlow.getScene().getWindow(), "/fxml/client/clientDashboard.fxml");
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
            statusLabel.setText("Inserisci una parola.");
            statusLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        this.connectionHandler.sendMessage(new Message(MessageType.gameAnswer, new RispostaGiocatoreDTO(answer)));

        System.out.println("Risposta inviata: " + answer);
        statusLabel.setText("Risposta inviata, in attesa di verifica...");
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