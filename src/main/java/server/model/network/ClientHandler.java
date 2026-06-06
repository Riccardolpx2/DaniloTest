package server.model.network;

import server.model.network.state.AuthState;
import server.model.network.state.ClientState;
import shared.protocol.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientState currentState;

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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Errore critico: impossibile deserializzare l'oggetto ricevuto.");
        } finally {
            // pulisciERimuoviClient();
        }
    }

    public ClientState getCurrentState() {
        return currentState;
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
