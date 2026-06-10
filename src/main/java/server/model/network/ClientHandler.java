package server.model.network;

import server.model.database.entity.UtenteEntity;
import server.model.network.state.AuthState;
import server.model.network.state.ClientState;
import server.logica.GameMatchHandler;
import shared.protocol.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientState currentState;
    private UtenteEntity loggedUser;
    private GameMatchHandler currentMatch;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        // Il primo stato è sempre quello di autorizzazione
        this.currentState = new AuthState();

        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();

            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message = (Message) in.readObject();

                currentState.handleMessage(message, this);
            }
        } catch (EOFException | SocketException e) {
            // Disconnessione gestita: il client ha chiuso l'app o si è disconnesso dalla rete
            System.out.println("Client disconnesso (IP: " + socket.getInetAddress() + ")");
        } catch (Exception e) {
            System.err.println("Errore critico durante la lettura dei messaggi dal client:");
            e.printStackTrace();
        } finally {
            cleanClient();
        }
    }

    private void cleanClient() {
        // Se l'utente era loggato e la connessione cade, lo liberiamo!
        if (loggedUser != null) {
            SessionManager.getInstance().logout(loggedUser.getUsername());
        }

        if (currentState != null) {
            currentState.onDisconnect(this);
        }
        if (currentMatch != null) {
            currentMatch.disconnettiClient();
        }

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura del socket: " + e.getMessage());
        }
    }

    public UtenteEntity getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(UtenteEntity loggedUser) {
        this.loggedUser = loggedUser;
    }

    public ClientState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ClientState currentState) {
        this.currentState = currentState;
    }

    public GameMatchHandler getCurrentMatch() {
        return currentMatch;
    }

    public void setCurrentMatch(GameMatchHandler currentMatch) {
        this.currentMatch = currentMatch;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public Socket getSocket() {
        return socket;
    }
}
