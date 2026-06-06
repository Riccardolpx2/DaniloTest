package server.model.network.state;

import server.model.database.UtenteDAO;
import server.model.network.ClientHandler;
import shared.protocol.Message;
import shared.protocol.MessageType;
import shared.protocol.DTO.LoginDTO;

public class AuthState extends ClientState{

    @MessageHandler(MessageType.login)
    private void login(Message message, ClientHandler clientHandler){
        LoginDTO payload = (LoginDTO) message.getPayload();

        String username = payload.getUsername();
        String password = payload.getPassword();

        UtenteDAO u = new UtenteDAO()

    }

}
