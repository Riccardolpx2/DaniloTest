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


    public ConnectionHandler(int port, String ipAddress) {
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public void setCurrentListener(Consumer<Message> currentListener) {
        this.currentListener = currentListener;
    }

    @Override
    public void run() {
        boolean connected = false;

        // FASE 1: Tentativi di connessione (massimo 5)
        for (int tentativi = 0; tentativi < 5 && running; tentativi++) {
            try {
                this.socket = new Socket(ipAddress, port);

                this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                this.out.flush();
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                System.out.println("Connessione al server riuscita su ip: " + socket.getInetAddress() + "  port: " + socket.getPort());
                connected = true;

                // Connessione stabilita con successo: usciamo dal ciclo dei tentativi
                break;

            } catch (IOException e) {
                // Se la chiusura è intenzionale durante i tentativi
                if (!running) {
                    return; // Esci direttamente
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

        // Se dopo i 5 tentativi (o se running è false) non siamo connessi, fermiamo tutto.
        if (!connected || !running) {
            System.err.println("Impossibile connettersi al server dopo 5 tentativi.");
            closeConnection();
            return;
        }

        // FASE 2: Lettura continua (nessun retry della connessione in caso di errore)
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
        currentListener.accept(message);
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }


    public void closeConnection() {
        this.running = false;
        try {
            // Chiusura sicura degli stream prima del socket
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura delle risorse: " + e.getMessage());
        }
    }
}