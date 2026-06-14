package client.controller;

import client.ClientApp;
import client.network.ConnectionHandler;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import shared.protocol.DTO.DomandaDTO;
import shared.protocol.DTO.EsitoRoundDTO;
import shared.protocol.DTO.EsitoPartitaDTO;
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

    // Elementi dell'Overlay dei Round
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

    // Elementi dell'Overlay di Fine PartitaEntity
    @FXML
    private VBox gameEndOverlay;

    @FXML
    private Label gameEndTitleLabel;

    @FXML
    private Label gameEndReasonLabel;

    @FXML
    private Label gameEndMyScoreLabel;

    @FXML
    private Label gameEndOpponentScoreLabel;

    private int timeRemaining = 30;
    private Timeline timeline;
    private String currentUser;
    private String usernameAvversario;

    public void inizializzaDati(String messaggio) {
        usernameAvversario = messaggio;
        Platform.runLater(() -> {
            if (sfidanteLabel != null) {
                sfidanteLabel.setText("Partita contro " + usernameAvversario);
            }
        });
    }

    @FXML
    public void initialize() {
        this.currentUser = ClientApp.getInstance().getCurrentUser();
        this.connectionHandler = ClientApp.getInstance().getConnectionHandler();
        this.connectionHandler.setCurrentListener(this::messageHandler);
    }

    private void messageHandler(Message message){
        switch(message.getMsgType()){
            case GAME_QUESTION:
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

                    if (timeline != null) {
                        timeline.stop();
                    }
                    timeRemaining = 30;
                    startTimer();
                });
                break;
            case GAME_ANSWER_RESULT:
                Platform.runLater(() -> {
                    if (timeline != null) {
                        timeline.stop();
                    }
                    EsitoRoundDTO esito = (EsitoRoundDTO) message.getPayload();
                    showRoundResult(esito);
                });
                break;
            case GAME_END:
                Platform.runLater(() -> {
                    if (timeline != null) {
                        timeline.stop();
                    }
                    // Adesso riceve correttamente il DTO di fine partita
                    EsitoPartitaDTO esitoPartita = (EsitoPartitaDTO) message.getPayload();
                    showGameEnd(esitoPartita);
                });
                break;
            default:
                System.out.println("Messaggio non gestito dal GameController: " + message.getMsgType());
        }
    }

    private void showRoundResult(EsitoRoundDTO esito) {
        answerField.setDisable(true);
        submitButton.setDisable(true);

        String vincitore = esito.getUsernameVincitore();

        // 1. Esito del round
        if (roundResultLabel != null) {
            if (vincitore == null) {
                roundResultLabel.setText("Nessuno ha indovinato! Pareggio");
                roundResultLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 38px; -fx-font-weight: bold;");
            } else if (vincitore.equals(currentUser)) {
                roundResultLabel.setText("Hai vinto il round!");
                roundResultLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 38px; -fx-font-weight: bold;");
            } else {
                roundResultLabel.setText("Ha vinto " + vincitore + "!");
                roundResultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 38px; -fx-font-weight: bold;");
            }
        }

        // 2. Parola corretta
        if (correctWordLabel != null) {
            correctWordLabel.setText("La parola corretta era: " + esito.getParolaSoluzione());
        }

        // 3. Gestione Punteggi
        int mioPunteggio = esito.getPunteggi().getOrDefault(currentUser, 0);
        int punteggioAvversario = esito.getPunteggi().getOrDefault(usernameAvversario, 0);

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

    private void showGameEnd(EsitoPartitaDTO esito) {
        // Nascondi l'overlay del round se era ancora visibile
        if (resultOverlay != null) {
            resultOverlay.setVisible(false);
        }

        String vincitore = esito.getUsernameVincitore();
        boolean abbandono = esito.isTerminataPerAbbandono();

        // 1. Titolo dell'esito finale e colori
        if (gameEndTitleLabel != null) {
            if (vincitore == null) {
                gameEndTitleLabel.setText("Partita Terminata: Pareggio!");
                gameEndTitleLabel.setStyle("-fx-text-fill: #f39c12;"); // Arancione
            } else if (vincitore.equals(currentUser)) {
                gameEndTitleLabel.setText("Hai Vinto la Partita!");
                gameEndTitleLabel.setStyle("-fx-text-fill: #2ecc71;"); // Verde
            } else {
                gameEndTitleLabel.setText("Hai Perso la Partita.");
                gameEndTitleLabel.setStyle("-fx-text-fill: #e74c3c;"); // Rosso
            }
        }

        // 2. Motivazione (Completamento o Abbandono)
        if (gameEndReasonLabel != null) {
            if (abbandono) {
                if (vincitore != null && vincitore.equals(currentUser)) {
                    gameEndReasonLabel.setText("Vittoria a tavolino: l'avversario ha abbandonato.");
                }
            } else {
                gameEndReasonLabel.setText("Tutti i round sono stati completati.");
            }
        }

        // 3. Punteggi Finali
        int mioPunteggio = esito.getPunteggiFinali().getOrDefault(currentUser, 0);
        int punteggioAvversario = esito.getPunteggiFinali().getOrDefault(usernameAvversario, 0);

        if (gameEndMyScoreLabel != null) {
            gameEndMyScoreLabel.setText(String.valueOf(mioPunteggio));
        }
        if (gameEndOpponentScoreLabel != null) {
            gameEndOpponentScoreLabel.setText(String.valueOf(punteggioAvversario));
        }

        // 4. Mostra l'overlay di fine partita
        if (gameEndOverlay != null) {
            gameEndOverlay.setVisible(true);
            gameEndOverlay.toFront();
        }
    }

    @FXML
    void returnToDashboard(ActionEvent event) {
        if (gameEndOverlay.getScene() != null && gameEndOverlay.getScene().getWindow() != null) {
            SceneManager.switchScene((javafx.stage.Stage) gameEndOverlay.getScene().getWindow(), "/fxml/client/clientDashboard.fxml");
        }
    }

    private void startTimer() {
        updateTimerLabel(timeRemaining);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            updateTimerLabel(timeRemaining);
            if (timeRemaining <= 0) {
                timeline.stop();
                timeUp();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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

        this.connectionHandler.sendMessage(new Message(MessageType.GAME_ANSWER_SUBMIT, new RispostaGiocatoreDTO(answer)));

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