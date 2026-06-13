package server.model.network.state;

import server.model.network.ClientHandler;
import server.model.network.SessionManager;
import shared.protocol.Message;
import shared.protocol.DTO.RispostaGiocatoreDTO;
import shared.protocol.MessageType;

/**
 * Stato del client attivo durante una partita (Match).
 * Permette la ricezione di risposte relative al round e gestisce eventuali
 * disconnessioni forzate o abbandoni durante il gioco.
 */
public class GameState extends ClientState{

    /**
     * Inoltra la risposta del giocatore al GameMatchHandler corrente.
     * @param message Il messaggio contenente la parola tentata.
     * @param clientHandler L'handler del client che ha risposto.
     */
    @MessageHandler(MessageType.gameAnswer)
    private void answer(Message message, ClientHandler clientHandler){
        if (clientHandler.getCurrentMatch() != null) {
            RispostaGiocatoreDTO risposta = (RispostaGiocatoreDTO) message.getPayload();
            clientHandler.getCurrentMatch().registraRisposta(clientHandler, risposta.getParolaTentata());
        }
    }

    /**
     * Intercetta la richiesta di logout dell'utente (abbandono). Termina forzatamente la partita.
     * @param message Il messaggio di richiesta logout.
     * @param clientHandler Il client che effettua il logout.
     */
    @MessageHandler(MessageType.logout)
    private void logout(Message message, ClientHandler clientHandler) {
        try {
            clientHandler.getCurrentMatch().terminaPartita(clientHandler);
            if (clientHandler.getLoggedUser() != null) {
                SessionManager.getInstance().logout(clientHandler.getLoggedUser().getUsername());
            }
            clientHandler.setLoggedUser(null);
            clientHandler.setCurrentState(new AuthState());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eseguito nel caso in cui il socket cada improvvisamente durante il gioco.
     * Assicura la disconnessione della sessione e la terminazione immediata della partita in corso.
     * @param clientHandler L'handler del client che si è disconnesso.
     */
    @Override
    public void onDisconnect(ClientHandler clientHandler) {
        System.out.println("[GameState] Disconnessione improvvisa del client in partita: " + 
                (clientHandler.getLoggedUser() != null ? clientHandler.getLoggedUser().getUsername() : "sconosciuto"));
        try {
            if (clientHandler.getCurrentMatch() != null) {
                clientHandler.getCurrentMatch().terminaPartita(clientHandler); // Forza la fine della partita per abbandono
            }
            if (clientHandler.getLoggedUser() != null) {
                SessionManager.getInstance().logout(clientHandler.getLoggedUser().getUsername());
            }
            clientHandler.setLoggedUser(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
