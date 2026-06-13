package server.model.network.state;

import server.model.network.MatchmakingManager;
import server.model.network.ClientHandler;
import server.model.network.SessionManager;
import server.model.service.DashboardService;
import shared.protocol.DTO.GameSearchDTO;
import shared.protocol.DTO.StatDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Stato del client dopo aver effettuato l'accesso (Login) con successo.
 * Gestisce le operazioni dal menu principale come la visualizzazione delle statistiche,
 * la messa in coda per il matchmaking e il logout.
 */
public class DashboardState extends ClientState{

    private DashboardService dashboardService;

    /**
     * Inizializza lo stato istanziando i servizi di dashboard.
     */
    public DashboardState() {
        this.dashboardService = new DashboardService();
    }

    /**
     * Risponde alla richiesta di estrazione delle statistiche dello storico partite per l'utente corrente.
     *
     * @param message Il messaggio in ingresso (nessun payload strettamente necessario).
     * @param clientHandler L'handler del client.
     */
    @MessageHandler(MessageType.STATS_REQUEST)
    private void stats(Message message, ClientHandler clientHandler) {

        String username = clientHandler.getLoggedUser().getUsername();
        try {
            StatDTO statDTO = dashboardService.getStatistiche(username);
            if(statDTO!=null){
                System.out.println("statistiche trovate" + statDTO.toString());
                // lato client gestire se non ci sono statistiche
                clientHandler.getOut().writeObject(new Message(MessageType.STATS_RESPONSE, statDTO));
            }else{
                System.out.println("statistiche non trovate");
                clientHandler.getOut().writeObject(new Message(MessageType.STATS_RESPONSE, null));
            }



        } catch (SQLException e) {
            try {
                clientHandler.getOut().writeObject(new Message(MessageType.GENERAL_ERROR, "Errore lato server, riprova riprova più tardi"));
            } catch (IOException ex) {
                System.out.println("Client disconnesso");
            }
        } catch (IOException e) {
            System.out.println("Client disconnesso");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Effettua il logout dell'utente, rimuovendolo dalla coda matchmaking se presente,
     * e lo rimanda allo stato {@link AuthState}.
     * @param message Il messaggio di logout.
     * @param clientHandler L'handler del client.
     */
    @MessageHandler(MessageType.LOGOUT_REQUEST)
    private void logout(Message message, ClientHandler clientHandler) {
        try {
            if (clientHandler.getLoggedUser() != null) {
                SessionManager.getInstance().logout(clientHandler.getLoggedUser().getUsername());
            }
            clientHandler.setLoggedUser(null);
            clientHandler.setCurrentState(new AuthState());
            MatchmakingManager.exitLobby(clientHandler); // per assicurarci di uscire dalla lobby (il metodo gestisce il fatto che non fosse in lobby)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Inserisce l'utente nella coda di Matchmaking del server tramite il {@link MatchmakingManager}.
     * @param message Il messaggio contenente la difficoltà richiesta (GameSearchDTO).
     * @param clientHandler L'handler del client.
     */
    @MessageHandler(MessageType.GAME_SEARCH_REQUEST)
    private void searchGame(Message message, ClientHandler clientHandler){
        MatchmakingManager.enterLobby(clientHandler, ((GameSearchDTO) message.getPayload()).getDifficoltaPartita());
    }

    /**
     * Rimuove il client dalla coda di Matchmaking se questi cancella la ricerca di partita.
     * @param message Il messaggio di cancellazione.
     * @param clientHandler L'handler del client.
     */
    @MessageHandler(MessageType.GAME_SEARCH_CANCEL)
    private void cancelSearchGame(Message message, ClientHandler clientHandler){
        MatchmakingManager.exitLobby(clientHandler);
    }

    /**
     * Libera le risorse associate alla dashboard se avviene una disconnessione di rete non prevista:
     * in particolare provvede a scollegare il client dalla coda d'attesa (se presente) ed eseguire il logout.
     * @param clientHandler L'handler del client disconnesso.
     */
    @Override
    public void onDisconnect(ClientHandler clientHandler) {
        System.out.println("[DashboardState] Disconnessione client: " + 
                (clientHandler.getLoggedUser() != null ? clientHandler.getLoggedUser().getUsername() : "sconosciuto"));
        try {
            MatchmakingManager.exitLobby(clientHandler); // Rimuove il client dalla coda se presente
            if (clientHandler.getLoggedUser() != null) {
                SessionManager.getInstance().logout(clientHandler.getLoggedUser().getUsername());
            }
            clientHandler.setLoggedUser(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
