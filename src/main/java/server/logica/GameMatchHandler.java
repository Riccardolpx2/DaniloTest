package server.logica;

import server.model.database.AnalisiTestoDAO;
import server.model.database.DocumentoDAO;
import server.model.network.ClientHandler;
import server.model.network.state.GameState;
import shared.game.AnalisiTesto;
import shared.game.Documento;
import shared.protocol.DTO.GameStartDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;
import shared.protocol.DTO.EsitoRoundDTO;
import shared.protocol.DTO.RispostaGiocatoreDTO;
import shared.protocol.DTO.TestoDTO;

import java.io.IOException;
import java.util.List;
import java.util.Random;

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
    }

    @Override
    public void run() {
        try {
            // 1. Inizializzazione della partita
            DocumentoDAO docDAO = new DocumentoDAO();
            AnalisiTestoDAO analisiDAO = new AnalisiTestoDAO();

            List<Documento> docs = docDAO.elencaTutti();
            if (docs.isEmpty()) {
                inviaErroreEntrambi("Nessun documento nel DB per iniziare la partita.");
                return;
            }
            
            // Prendiamo un documento casuale
            Documento randomDoc = docs.get(new Random().nextInt(docs.size()));

            // Cerchiamo l'analisi associata a quel documento
            List<AnalisiTesto> analisiList = analisiDAO.elencaTutti();
            AnalisiTesto analisi = analisiList.stream()
                    .filter(a -> a.getIdDocumento() == randomDoc.getIdDocumento())
                    .findFirst()
                    .orElse(null);

            // Se non c'è, la creiamo al volo (fallback)
            if (analisi == null) {
                analisi = new AnalisiTesto(randomDoc.getIdDocumento());
                analisi.analizza(randomDoc.getTesto());
            }

            // 2. Creazione della logica di Match e transizione di stato
            this.matchManager = new MatchManager(
                    player1.getLoggedUser(), player2.getLoggedUser(), randomDoc, "MEDIA", analisi
            );

            player1.setCurrentMatch(this);
            player2.setCurrentMatch(this);
            player1.setCurrentState(new GameState());
            player2.setCurrentState(new GameState());

            // 3. Notifica di inizio ai client e invio info avversari
            player1.getOut().writeObject(new Message(MessageType.gameStart, new GameStartDTO(player2.getLoggedUser().getUsername(), "MEDIA")));
            player2.getOut().writeObject(new Message(MessageType.gameStart, new GameStartDTO(player1.getLoggedUser().getUsername(), "MEDIA")));
            Thread.sleep(2000);

            // 4. Ciclo dei Round
            for (int i = 0; i < NUM_ROUNDS && matchRunning; i++) {
                if(!giocaRound()) {
                    break; // interruzione anomala
                }
            }

            // 5. Chiusura Sessione e salvataggio risultati
            if (matchRunning) {
                matchManager.terminaSessione();
                inviaMessaggioEntrambi(new Message(MessageType.gameEnd, "Partita terminata!"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            inviaErroreEntrambi("Errore critico durante la partita.");
        }
    }

    private boolean giocaRound() throws Exception {
        rispostaP1 = null;
        rispostaP2 = null;

        TestoDTO testoCifrato = matchManager.inizializzaNuovoRound();
        inviaMessaggioEntrambi(new Message(MessageType.gameQuestion, testoCifrato));

        long timeoutMillis = 30000;
        this.roundStartTime = System.currentTimeMillis();

        // Attesa risposte o timeout
        synchronized (this) {
            while ((rispostaP1 == null || rispostaP2 == null) &&
                   (System.currentTimeMillis() - roundStartTime) < timeoutMillis &&
                   matchRunning) {
                long waitTime = timeoutMillis - (System.currentTimeMillis() - roundStartTime);
                if(waitTime > 0) this.wait(waitTime);
            }
        }

        if (!matchRunning) return false;

        // Se scade il tempo e qualcuno non ha risposto, assegniamo una risposta fittizia "Sbagliata" a tempo massimo
        if (rispostaP1 == null) rispostaP1 = new RispostaGiocatoreDTO("", 30);
        if (rispostaP2 == null) rispostaP2 = new RispostaGiocatoreDTO("", 30);

        EsitoRoundDTO esito = matchManager.registraEsitoRound(rispostaP1, rispostaP2);
        inviaMessaggioEntrambi(new Message(MessageType.gameResponse, esito));

        Thread.sleep(4000); // Pausa di visualizzazione esito prima del prossimo round
        return true;
    }

    // Metodo thread-safe chiamato dal GameState quando un utente dà la risposta
    public synchronized void registraRisposta(ClientHandler client, String parolaTentata) {
        int tempoImpiegato = (int) ((System.currentTimeMillis() - roundStartTime) / 1000);
        if (client.equals(player1) && rispostaP1 == null) rispostaP1 = new RispostaGiocatoreDTO(parolaTentata, tempoImpiegato);
        else if (client.equals(player2) && rispostaP2 == null) rispostaP2 = new RispostaGiocatoreDTO(parolaTentata, tempoImpiegato);
        
        if (rispostaP1 != null && rispostaP2 != null) this.notifyAll(); // Entrambi hanno risposto, sblocca il round!
    }

    public synchronized void disconnettiClient() {
        matchRunning = false;
        this.notifyAll();
    }

    private void inviaMessaggioEntrambi(Message msg) {
        try { player1.getOut().writeObject(msg); player1.getOut().flush(); } catch (IOException ignored) {}
        try { player2.getOut().writeObject(msg); player2.getOut().flush(); } catch (IOException ignored) {}
    }

    private void inviaErroreEntrambi(String errorStr) {
        inviaMessaggioEntrambi(new Message(MessageType.gameError, errorStr));
    }
}