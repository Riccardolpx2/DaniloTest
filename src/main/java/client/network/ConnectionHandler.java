package client.network;

import javafx.application.Platform;
import shared.protocol.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class ConnectionHandler implements Runnable {

    private Socket socket;
    private int port;
    private String ipAddress;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Consumer<Message> currentListener;
    private volatile boolean running = true;
    private volatile boolean connected = false;

    // Aggiunta di callback per gestire l'UI dinamicamente
    private Runnable onConnectionSuccess;
    private Runnable onConnectionFailed;

    public ConnectionHandler(int port, String ipAddress) {
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public void setCurrentListener(Consumer<Message> currentListener) {
        this.currentListener = currentListener;
    }

    public void setConnectionCallbacks(Runnable onSuccess, Runnable onFailed) {
        this.onConnectionSuccess = onSuccess;
        this.onConnectionFailed = onFailed;
    }

    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void run() {
        // FASE 1: Tentativi di connessione (massimo 5)
        for (int tentativi = 0; tentativi < 5 && running; tentativi++) {
            try {
                this.socket = new Socket(ipAddress, port);

                this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                this.out.flush();
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                System.out.println("Connessione al server riuscita su ip: " + socket.getInetAddress() + "  port: " + socket.getPort());
                this.connected = true;

                // Connessione stabilita con successo: avvisiamo la UI
                if (onConnectionSuccess != null) {
                    Platform.runLater(onConnectionSuccess);
                }
                break;

            } catch (IOException e) {
                if (!running) {
                    return;
                }

                System.out.println("Tentativo di connessione " + (tentativi + 1) + " fallito. Attendo prima di riprovare...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrotto durante l'attesa. Riavvia l'app", ex);
                }
            }
        }

        // Se dopo i 5 tentativi non siamo connessi
        if (!connected || !running) {
            System.err.println("Impossibile connettersi al server dopo 5 tentativi.");

            // Invochiamo la callback di fallimento per mostrare l'overlay di errore
            if (!connected && running && onConnectionFailed != null) {
                Platform.runLater(onConnectionFailed);
            }

            closeConnection();
            return;
        }

        // FASE 2: Lettura continua
        try {
            while (running) {
                handleMessage((Message) in.readObject());
            }
        } catch (IOException e) {
            if (!running) {
                System.out.println("Chiusura del listener intenzionale.");
            } else {
                System.err.println("Connessione persa durante la lettura dei messaggi. Disconnessione in corso...");
            }
        } catch (ClassNotFoundException e) {
            if (running) {
                throw new RuntimeException("Errore di deserializzazione del messaggio", e);
            }
        } finally {
            closeConnection();
        }
    }

    private void handleMessage(Message message) {
        if (currentListener != null) {
            currentListener.accept(message);
        }
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    public void closeConnection() {
        this.running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura delle risorse: " + e.getMessage());
        }
    }
}