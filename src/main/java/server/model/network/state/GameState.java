package server.model.network.state;

import server.model.network.ClientHandler;
import shared.protocol.Message;
import shared.protocol.MessageType;

public class GameState extends ClientState{

    @MessageHandler(MessageType.gameAnswer)
    private void answer(Message message, ClientHandler clientHandler){
        if (clientHandler.getCurrentMatch() != null) {
            clientHandler.getCurrentMatch().registraRisposta(clientHandler, (String) message.getPayload());
        }
    }
}
