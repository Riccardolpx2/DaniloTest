package server.logica;

import server.model.network.ClientHandler;
import server.model.network.state.AuthState;
import server.model.network.state.GameState;
import shared.protocol.DTO.DomandaDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;
import shared.protocol.DTO.RispostaGiocatoreDTO;

import java.io.IOException;
import java.sql.SQLException;

public class GameMatchHandler implements Runnable {

    private final ClientHandler player1;
    private final ClientHandler player2;
    private MatchManager matchManager;
    private Domanda domandaCorrente;

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

        player1.setCurrentState(new GameState());
        player2.setCurrentState(new GameState());
    }

    @Override
    public void run() {
        while(matchRunning){
            this.domandaCorrente = this.matchManager.getDomanda();
            if(domandaCorrente == null) {
                this.matchRunning = false;
                //da modificare che manda il vincitore, punti, ecc.
                inviaMessaggioEntrambi(new Message(MessageType.gameEnd, null));
                break;
            }
            inviaMessaggioEntrambi(new Message(MessageType.gameQuestion, new DomandaDTO(this.domandaCorrente)));



        }

    }

    private boolean giocaRound() throws Exception {
        return false;
    }


    public synchronized void registraRisposta(ClientHandler client, String parolaTentata) {
        if (this.domandaCorrente.getParoleSoluzioni().contains(parolaTentata)){

        }
    }

    public synchronized void disconnettiClient() {
        // Mi assicuro di terminare il thread
        this.matchRunning = false;
        inviaMessaggioEntrambi(new Message(MessageType.gameError, "Errore durante la partita"));
    }

    private void inviaMessaggioEntrambi(Message msg) {
        try { player1.getOut().writeObject(msg); player1.getOut().flush(); } catch (IOException ignored) {}
        try { player2.getOut().writeObject(msg); player2.getOut().flush(); } catch (IOException ignored) {}
    }

    private void inviaDomandaEntrambi(Domanda domanda){
        DomandaDTO domandaDTO = new DomandaDTO(domanda.getTestoCifrato(), domanda.getParoleSoluzioniCifrate());
        inviaMessaggioEntrambi(new Message(MessageType.gameQuestion, domandaDTO));
    }

}