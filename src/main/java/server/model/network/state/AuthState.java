package server.model.network.state;

import server.model.database.UtenteDAO;
import server.model.database.entity.UtenteEntity;
import server.model.network.ClientHandler;
import server.model.service.AuthService;
import shared.protocol.DTO.RegisterDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;
import shared.protocol.DTO.LoginDTO;

import java.io.IOException;
import java.sql.SQLException;

public class AuthState extends ClientState{

    private final AuthService authService;

    public AuthState(){
        this.authService = new AuthService();
    }

    // TODO: documentazione
    @MessageHandler(MessageType.login)
    private void login(Message message, ClientHandler clientHandler){
        LoginDTO payload = (LoginDTO) message.getPayload();

        String username = payload.getUsername();
        String password = payload.getPassword();

        try {
            UtenteEntity utenteEntity = authService.login(username, password);
            if (utenteEntity != null){
                // associo l'utente alla socket tcp
                clientHandler.setLoggedUser(utenteEntity);
                // Serve per cambiare lo stato
                clientHandler.setCurrentState(new DashboardState());
                clientHandler.getOut().writeObject(new Message(MessageType.loginSuccess, null));
            } else {
                clientHandler.getOut().writeObject(new Message(MessageType.loginFailure, "Username o Password errati"));
            }
        } catch(SQLException e){
            try {
                clientHandler.getOut().writeObject(new Message(MessageType.generalError, "Errore lato server, riprova riprova più tardi"));
            } catch (IOException ex) {
                System.out.println("Client disconnesso");
            }
        } catch (IOException e){
            System.out.println("Client disconnesso");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @MessageHandler(MessageType.register)
    private void register(Message message, ClientHandler clientHandler){
        RegisterDTO payload = (RegisterDTO) message.getPayload();

        String username = payload.getUsername();
        String password = payload.getPassword();
        String nome = payload.getNome();
        String cognome = payload.getCognome();
        String dataNascita = payload.getDataNascita();

        try {
            if (authService.register(username, password, nome, cognome, dataNascita)){
                clientHandler.getOut().writeObject(new Message(MessageType.registerSuccess, null));
            } else{
                clientHandler.getOut().writeObject(new Message(MessageType.registerFailure, "Utente già registrato"));
            }
        } catch (SQLException e){
            try{
                clientHandler.getOut().writeObject(new Message(MessageType.generalError, "Errore lato server, riprova più tardi"));
            } catch (IOException ex) {
                System.out.println("Client disconnesso");
            }
        } catch (IOException e){
            System.out.println("Client disconnesso");
        }
    }



}
