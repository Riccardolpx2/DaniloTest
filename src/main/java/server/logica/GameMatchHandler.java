package server.logica;

import server.model.network.ClientHandler;
import shared.protocol.Message;
import shared.protocol.MessageType;
import shared.protocol.DTO.RispostaGiocatoreDTO;

import java.io.IOException;
import java.sql.SQLException;

public class GameMatchHandler implements Runnable {

    private final ClientHandler player1;
    private final ClientHandler player2;
    private MatchManager matchManager;

    private RispostaGiocatoreDTO rispostaP1;
    private RispostaGiocatoreDTO rispostaP2;

    private boolean matchRunning = true;
    private final int NUM_ROUNDS = 5;
    private long roundStartTime;

    public GameMatchHandler(ClientHandler player1, ClientHandler player2) {
        this.player1 = player1;
        this.player2 = player2;
        GameFactory gameFactory = new GameFactory();
        try {
            this.matchManager = gameFactory.creaMatch(player1.getLoggedUser(), player2.getLoggedUser(),
                    "MEDIA", NUM_ROUNDS);
        } catch (SQLException e) {
            disconnettiClient();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {

    }

    private boolean giocaRound() throws Exception {
        return false;
    }


    public synchronized void registraRisposta(ClientHandler client, String parolaTentata) {

    }

    public synchronized void disconnettiClient() {
        player1.getOut().writeObject();
    }

    private void inviaMessaggioEntrambi(Message msg) {
        try { player1.getOut().writeObject(msg); player1.getOut().flush(); } catch (IOException ignored) {}
        try { player2.getOut().writeObject(msg); player2.getOut().flush(); } catch (IOException ignored) {}
    }

    private void inviaErroreEntrambi(String errorStr) {
        inviaMessaggioEntrambi(new Message(MessageType.gameError, errorStr));
    }
}