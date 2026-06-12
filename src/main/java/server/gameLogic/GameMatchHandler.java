package server.gameLogic;

import server.model.network.ClientHandler;
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
    private Domanda domandaCorrente;

    private RispostaGiocatoreDTO rispostaP1;
    private RispostaGiocatoreDTO rispostaP2;
    private int tempoP1;
    private int tempoP2;

    private boolean matchRunning = true;
    private final int NUM_ROUNDS = 5;
    private long roundStartTime;
    private boolean roundConcluso = false;

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
            Thread.sleep(2000); // Pausa di 2 secondi per permettere ai client di caricare la GUI del gioco
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        while(matchRunning){
            this.domandaCorrente = this.matchManager.getDomanda();
            if(domandaCorrente == null) {
                this.matchRunning = false;
                try {
                    matchManager.terminaSessione();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //String fineMsg = "Partita Terminata! " + matchManager.getPunteggioG1() + " - " + matchManager.getPunteggioG2();
                String fineMsg = "";
                inviaMessaggioEntrambi(new Message(MessageType.gameEnd, fineMsg));
                break;
            }

            this.rispostaP1 = null;
            this.rispostaP2 = null;
            this.tempoP1 = 30; // Tempo di default se non si risponde
            this.tempoP2 = 30; // Tempo di default se non si risponde
            this.roundConcluso = false;
            this.roundStartTime = System.currentTimeMillis();

            inviaMessaggioEntrambi(new Message(MessageType.gameQuestion, new DomandaDTO(this.domandaCorrente)));
            
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
                // Se il tempo è scaduto o i giocatori non hanno risposto, crea risposte fittizie con 30 sec
                if (rispostaP1 == null) rispostaP1 = new RispostaGiocatoreDTO("");
                if (rispostaP2 == null) rispostaP2 = new RispostaGiocatoreDTO("");

                esito = matchManager.registraEsitoRound(rispostaP1, tempoP1, rispostaP2, tempoP2);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (esito != null) {
                inviaMessaggioEntrambi(new Message(MessageType.gameResponse, esito));
            }

            try {
                Thread.sleep(5000); // Pausa di 5 secondi per mostrare l'esito prima della prossima domanda
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }


    public synchronized void registraRisposta(ClientHandler client, String parolaTentata) {
        //if (!matchRunning || roundConcluso) return; // Evita di registrare risposte fuori dal round

        // Per fare prova
        EsitoRoundDTO esitoRoundDTO = new EsitoRoundDTO(null, "Nerchia", 69, 90);
        inviaMessaggioEntrambi(new Message(MessageType.gameResponse, esitoRoundDTO));
        roundConcluso = true;
        System.out.println("Ho inviato il messaggio di gameResponse");
        this.notifyAll();

        // *************

//        int tempoImpiegato = (int) ((System.currentTimeMillis() - roundStartTime) / 1000);
//        RispostaGiocatoreDTO risposta = new RispostaGiocatoreDTO(parolaTentata);
//
//        boolean isCorretta = this.domandaCorrente.getParoleSoluzioni().contains(parolaTentata.trim().toLowerCase());
//
//        if (client == player1 && rispostaP1 == null) {
//            rispostaP1 = risposta;
//            tempoP1 = tempoImpiegato;
//        } else if (client == player2 && rispostaP2 == null) {
//            rispostaP2 = risposta;
//            tempoP2 = tempoImpiegato;
//        }
//
//        // Se uno dei due dà la risposta corretta, o se entrambi hanno esaurito i loro tentativi, chiudiamo il round
//        if (isCorretta || (rispostaP1 != null && rispostaP2 != null)) {
//            roundConcluso = true;
//            this.notifyAll(); // Sblocca istantaneamente la wait(30000) nel run()
//        }
    }

    public synchronized void disconnettiClient() {
        // Mi assicuro di terminare il thread
        this.matchRunning = false;
        this.notifyAll(); // Sblocca l'attesa in caso di disconnessione improvvisa
        inviaMessaggioEntrambi(new Message(MessageType.gameError, "Errore durante la partita"));
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
            inviaMessaggioEntrambi(new Message(MessageType.gameError, "Errore"));
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

    public synchronized void abbandonaPartita(ClientHandler quitter) {
        if (!matchRunning) return;
        this.matchRunning = false;
        this.notifyAll();

        ClientHandler winner = (quitter == player1) ? player2 : player1;
        
        try {
            matchManager.terminaSessione();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        inviaMessaggioEntrambi(new Message(MessageType.gameEnd, 
            "Partita terminata: " + quitter.getLoggedUser().getUsername() + " ha abbandonato. Vince " + winner.getLoggedUser().getUsername() + "!"));
    }

}
