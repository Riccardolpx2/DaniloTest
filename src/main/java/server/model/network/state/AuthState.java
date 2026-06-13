package server.model.network.state;

import server.model.database.entity.UtenteEntity;
import server.model.network.ClientHandler;
import server.model.network.SessionManager;
import server.model.service.AuthService;
import shared.protocol.DTO.RegisterDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;
import shared.protocol.DTO.LoginDTO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Stato iniziale del client. Gestisce le operazioni di autenticazione e registrazione.
 * Quando l'utente effettua il login con successo, lo stato del client transita verso
 * {@link DashboardState}.
 */
public class AuthState extends ClientState{

    private final AuthService authService;

    /**
     * Costruisce lo stato di autenticazione inizializzando i servizi necessari.
     */
    public AuthState(){
        this.authService = new AuthService();
    }

    /**
     * Gestisce la richiesta di login di un utente. Controlla le credenziali tramite database
     * e previene il doppio accesso usando il {@link SessionManager}.
     *
     * @param message Il messaggio contenente il payload del login (LoginDTO).
     * @param clientHandler Il client che ha richiesto il login.
     */
    @MessageHandler(MessageType.LOGIN_REQUEST)
    private void login(Message message, ClientHandler clientHandler){
        LoginDTO payload = (LoginDTO) message.getPayload();

        String username = payload.getUsername();
        String password = payload.getPassword();

        try {
            UtenteEntity utenteEntity = authService.login(username, password);
            if (utenteEntity != null){

                // Controlliamo se l'utente ha già una sessione attiva
                if (!SessionManager.getInstance().login(username, clientHandler)) {
                    clientHandler.getOut().writeObject(new Message(MessageType.LOGIN_FAILURE, "Utente già connesso."));
                    return;
                }

                // associo l'utente alla socket tcp
                clientHandler.setLoggedUser(utenteEntity);
                // Serve per cambiare lo stato
                clientHandler.setCurrentState(new DashboardState());
                clientHandler.getOut().writeObject(new Message(MessageType.LOGIN_SUCCESS, null));
            } else {
                clientHandler.getOut().writeObject(new Message(MessageType.LOGIN_FAILURE, "Username o Password errati"));
            }
        } catch(SQLException e){
            try {
                clientHandler.getOut().writeObject(new Message(MessageType.GENERAL_ERROR, "Errore lato server, riprova riprova più tardi"));
            } catch (IOException ex) {
                System.out.println("Client disconnesso");
            }
        } catch (IOException e){
            System.out.println("Client disconnesso");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestisce la richiesta di registrazione di un nuovo utente. Verifica che
     * l'utente non esista già e tenta il salvataggio sul database.
     *
     * @param message Il messaggio contenente il payload della registrazione (RegisterDTO).
     * @param clientHandler Il client che richiede la registrazione.
     */
    @MessageHandler(MessageType.REGISTER_REQUEST)
    private void register(Message message, ClientHandler clientHandler){
        RegisterDTO payload = (RegisterDTO) message.getPayload();

        String username = payload.getUsername();
        String password = payload.getPassword();
        String nome = payload.getNome();
        String cognome = payload.getCognome();
        String dataNascita = payload.getDataNascita();

        try {
            if (authService.register(username, password, nome, cognome, dataNascita)){
                clientHandler.getOut().writeObject(new Message(MessageType.REGISTER_SUCCESS, null));
            } else{
                clientHandler.getOut().writeObject(new Message(MessageType.REGISTER_FAILURE, "Utente già registrato"));
            }
        } catch (SQLException e){
            try{
                clientHandler.getOut().writeObject(new Message(MessageType.GENERAL_ERROR, "Errore lato server, riprova più tardi"));
            } catch (IOException ex) {
                System.out.println("Client disconnesso");
            }
        } catch (IOException e){
            System.out.println("Client disconnesso");
        }
    }



}
