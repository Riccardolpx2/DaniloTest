package server.model.network.state;

import server.model.network.ClientHandler;
import server.model.network.SessionManager;
import shared.protocol.Message;
import shared.protocol.DTO.RispostaGiocatoreDTO;
import shared.protocol.MessageType;

public class GameState extends ClientState{

    @MessageHandler(MessageType.gameAnswer)
    private void answer(Message message, ClientHandler clientHandler){
        if (clientHandler.getCurrentMatch() != null) {
            RispostaGiocatoreDTO risposta = (RispostaGiocatoreDTO) message.getPayload();
            clientHandler.getCurrentMatch().registraRisposta(clientHandler, risposta.getParolaTentata());
        }
    }

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
}
