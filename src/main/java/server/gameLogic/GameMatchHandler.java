package server.gameLogic;

import server.model.network.ClientHandler;
import server.model.network.state.DashboardState;
import server.model.network.state.GameState;
import shared.protocol.DTO.DomandaDTO;
import shared.protocol.DTO.GameStartDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;
import shared.protocol.DTO.RispostaGiocatoreDTO;
import shared.protocol.DTO.EsitoRoundDTO;

import java.io.IOException;
import java.sql.SQLException;

public class GameMatchHandler implements Runnable {

    private final ClientHandler player1;
    private final ClientHandler player2;
    private final MatchManager matchManager;
    private final String difficolta;

    private boolean matchRunning = true;
    private final int NUM_ROUNDS = 5;

    private long roundStartTime;
    private boolean roundConcluso = false;

    // Variabili per tracciare chi ha già risposto in questo round ed evitare risposte multiple
    private boolean haRispostoP1 = false;
    private boolean haRispostoP2 = false;

    public GameMatchHandler(ClientHandler player1, ClientHandler player2, String difficolta) {
        this.player1 = player1;
        this.player2 = player2;
        this.difficolta = difficolta;
        GameFactory gameFactory = new GameFactory();
        try {
            this.matchManager = gameFactory.creaMatch(player1.getLoggedUser(), player2.getLoggedUser(),
                    difficolta, NUM_ROUNDS);
        } catch (SQLException e) {
            disconnettiClient();
            throw new RuntimeException(e);
        }

        player1.setCurrentState(new GameState());
        player1.setCurrentMatch(this);
        player2.setCurrentState(new GameState());
        player2.setCurrentMatch(this);
    }

    @Override
    public void run() {
        inviaInizioPartita();

        try {
            Thread.sleep(1000); // Pausa per permettere ai client di caricare la GUI del gioco
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        while(matchRunning){
            // Chiediamo al manager di preparare il round. Se ritorna null, la partita è finita
            Domanda domandaCorrente = this.matchManager.iniziaNuovoRound();

            if(domandaCorrente == null) {
                this.matchRunning = false;

                // 1. FINE NATURALE: Riportiamo subito i client in Dashboard
                riportaClientInDashboard();

                try {
                    matchManager.terminaPartita(null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                int puntiP1 = matchManager.punteggioAttualeDi(player1.getLoggedUser());
                int puntiP2 = matchManager.punteggioAttualeDi(player2.getLoggedUser());
                String fineMsg = "Partita Terminata! " + puntiP1 + " - " + puntiP2;

                inviaMessaggioEntrambi(new Message(MessageType.gameEnd, fineMsg));
                break;
            }

            // Reset dello stato del thread per il nuovo round
            this.haRispostoP1 = false;
            this.haRispostoP2 = false;
            this.roundConcluso = false;
            this.roundStartTime = System.currentTimeMillis();

            inviaMessaggioEntrambi(new Message(MessageType.gameQuestion, new DomandaDTO(domandaCorrente)));

            synchronized (this) {
                try {
                    this.wait(30000); // Attende per massimo 30 secondi
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (!matchRunning) break; // Se nel frattempo la partita è stata forzata a interrompersi

            EsitoRoundDTO esito = null;
            try {
                // Il Manager sa già chi ha risposto e chi no, e genera l'esito
                esito = matchManager.chiudiRound();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (esito != null) {
                inviaMessaggioEntrambi(new Message(MessageType.gameResponse, esito));
            }

            try {
                Thread.sleep(5000); // Pausa per mostrare l'esito prima della prossima domanda
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    public synchronized void registraRisposta(ClientHandler client, String parolaTentata) {
        if (!matchRunning || roundConcluso) return;

        boolean isP1 = (client == player1);

        if (isP1 && haRispostoP1) return;
        if (!isP1 && haRispostoP2) return;

        if (isP1) haRispostoP1 = true;
        else haRispostoP2 = true;

        int tempoImpiegato = (int) ((System.currentTimeMillis() - roundStartTime) / 1000);
        RispostaGiocatoreDTO risposta = new RispostaGiocatoreDTO(parolaTentata);

        boolean isCorretta = matchManager.elaboraRisposta(client.getLoggedUser(), risposta, tempoImpiegato);

        if (isCorretta || (haRispostoP1 && haRispostoP2)) {
            roundConcluso = true;
            this.notifyAll();
        }
    }

    public synchronized void disconnettiClient() {
        if (!matchRunning) return; // Evita esecuzioni multiple se crashano insieme

        this.matchRunning = false;

        // 2. DISCONNESSIONE/CRASH: Riportiamo in Dashboard chiunque sia rimasto
        riportaClientInDashboard();

        this.notifyAll(); // Sblocca l'attesa in caso di disconnessione improvvisa
        inviaMessaggioEntrambi(new Message(MessageType.gameError, "Errore durante la partita"));
    }

    public synchronized void abbandonaPartita(ClientHandler quitter) {
        if (!matchRunning) return;
        this.matchRunning = false;

        // 3. ABBANDONO VOLONTARIO: Ripristina gli stati di entrambi
        riportaClientInDashboard();

        this.notifyAll();

        ClientHandler winner = (quitter == player1) ? player2 : player1;

        try {
            matchManager.terminaPartita(quitter.getLoggedUser());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // TODO: Da inserire il Payload corretto con punteggi ecc.
        inviaMessaggioEntrambi(new Message(MessageType.gameEnd,
                "Partita terminata: " + quitter.getLoggedUser().getUsername() + " ha abbandonato. Vince " + winner.getLoggedUser().getUsername() + "!"));
    }

    /**
     * Metodo Helper: ripristina in sicurezza lo stato di entrambi i client alla Dashboard
     * e ripulisce il riferimento alla partita ormai conclusa.
     */
    private void riportaClientInDashboard() {
        player1.setCurrentState(new DashboardState());
        player1.setCurrentMatch(null);

        player2.setCurrentState(new DashboardState());
        player2.setCurrentMatch(null);
    }

    private void inviaInizioPartita(){
        try {
            player1.getOut().writeObject(new Message(MessageType.gameStart,
                    new GameStartDTO(player2.getLoggedUser().getUsername(), difficolta)));
            player1.getOut().flush();

            player2.getOut().writeObject(new Message(MessageType.gameStart,
                    new GameStartDTO(player1.getLoggedUser().getUsername(), difficolta)));
            player2.getOut().flush();
        } catch (Exception e) {
            inviaMessaggioEntrambi(new Message(MessageType.gameError, "Errore di connessione"));
            disconnettiClient();
        }
    }

    private void inviaMessaggioClient(ClientHandler player, Message msg){
        try { player.getOut().writeObject(msg); player.getOut().flush(); } catch (IOException ignored) {}
    }

    private void inviaMessaggioEntrambi(Message msg) {
        inviaMessaggioClient(player1, msg);
        inviaMessaggioClient(player2, msg);
    }

    private void inviaDomandaEntrambi(Domanda domanda){
        DomandaDTO domandaDTO = new DomandaDTO(domanda.getTestoCifrato(), domanda.getParoleSoluzioniCifrate());
        inviaMessaggioEntrambi(new Message(MessageType.gameQuestion, domandaDTO));
    }
}