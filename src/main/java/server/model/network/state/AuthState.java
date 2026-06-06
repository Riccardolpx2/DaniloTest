package server.model.network.state;

import server.model.network.ClientHandler;
import shared.model.protocol.Message;
import shared.model.protocol.MessageType;
import shared.model.protocol.payload.LoginDTO;

public class AuthState extends ClientState{

    @MessageHandler(MessageType.login)
    private void login(Message message, ClientHandler clientHandler){
        LoginDTO payload = (LoginDTO) message.getPayload();

        String username = payload.getUsername();
        String password = payload.getPassword();



    }

}
