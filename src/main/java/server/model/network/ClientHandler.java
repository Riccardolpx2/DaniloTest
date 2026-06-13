package server.model.network;

import server.model.database.entity.UtenteEntity;
import server.model.network.state.AuthState;
import server.model.network.state.ClientState;
import shared.protocol.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.Socket;

/**
 * Gestisce la comunicazione di rete con un singolo client connesso.
 * Implementa {@link Runnable} per essere eseguito in un thread dedicato.
 * Mantiene lo stato corrente del client (Pattern State), i riferimenti agli stream
 * di I/O, l'utente autenticato e l'eventuale partita in corso.
 */
public class ClientHandler implements Runnable{

    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientState currentState;
    private UtenteEntity loggedUser;
    private GameMatchHandler currentMatch;

    /**
     * Costruisce un nuovo handler per il client connesso.
     *
     * @param socket Il socket generato dall'accettazione della connessione.
     */
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

    /**
     * Ciclo di vita principale del thread. Resta in ascolto di nuovi messaggi in ingresso
     * dal client e li inoltra allo stato corrente per la corretta gestione.
     */
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

    /**
     * Effettua la pulizia delle risorse al momento della disconnessione del client.
     * Disconnette l'utente se loggato, avvisa lo stato e la partita corrente della
     * disconnessione e chiude in sicurezza gli stream e il socket.
     */
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

        // Chiudiamo le risorse singolarmente. Ignoriamo le eccezioni perché
        // in fase di disconnessione non c'è nulla da recuperare se la chiusura fallisce.
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException ignored) {}
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
