package server.model.network;

import java.util.HashMap;
import java.util.Map;


public class MatchmakingManager {
    private static final Map<String, ClientHandler> lobbies = new HashMap<>();

    public static synchronized void enterLobby(ClientHandler clientHandler, String difficolta) {
        String key = difficolta.toUpperCase();
        ClientHandler waitingClient = lobbies.get(key);

        if (waitingClient == null) {
            lobbies.put(key, clientHandler);
            System.out.println("Client " + clientHandler.getLoggedUser().getUsername() + " in attesa per difficoltà: " + key);
        } else {
            lobbies.remove(key);
            
            System.out.println("Match trovato per difficoltà " + key + ": " + 
                               waitingClient.getLoggedUser().getUsername() + " vs " + 
                               clientHandler.getLoggedUser().getUsername());

            GameMatchHandler match = new GameMatchHandler(waitingClient, clientHandler, key);
            Thread matchThread = new Thread(match);
            matchThread.setDaemon(true);
            matchThread.start();
        }
    }

    public static synchronized void exitLobby(ClientHandler clientHandler) {
        String keyToRemove = null;
        for (Map.Entry<String, ClientHandler> entry : lobbies.entrySet()) {
            if (entry.getValue().equals(clientHandler)) {
                keyToRemove = entry.getKey();
                break;
            }
        }

        if (keyToRemove != null) {
            lobbies.remove(keyToRemove);
            System.out.println("Client " + clientHandler.getLoggedUser().getUsername() + " ha lasciato la lobby " + keyToRemove);
        } else {
            System.out.println("Il client " + clientHandler.getLoggedUser().getUsername() + " non era in attesa di alcuna partita.");
        }
    }
}
