package server.logica;

import server.model.network.ClientHandler;
import shared.protocol.Message;
import shared.protocol.MessageType;

public class MatchmakingManager {
    private ClientHandler clientWaiting;

    public synchronized void enterLobby(ClientHandler clientHandler){
        if (clientWaiting == null){
            clientWaiting = clientHandler;
        } else{
            ClientHandler p1 = clientWaiting;
            ClientHandler p2 = clientHandler;
            clientWaiting = null;

            GameMatchHandler match = new GameMatchHandler(p1, p2);
            Thread matchThread = new Thread(match);
            matchThread.setDaemon(true);
            matchThread.start();
        }
    }


}
