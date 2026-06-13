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

/**
 * Gestisce la connessione di rete lato client verso il server.
 * Implementa {@link Runnable} per eseguire in un thread dedicato il ciclo di ascolto
 * continuo dei messaggi in ingresso. Si occupa inoltre di instaurare la connessione
 * (con una logica di retry automatico) e di fornire i metodi per l'invio e la
 * ricezione dei messaggi ({@link Message}).
 */
public class ConnectionHandler implements Runnable {

    private Socket socket;
    private int port;
    private String ipAddress;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Consumer<Message> currentListener;
    private volatile boolean running = true;


    /**
     * Costruisce il gestore della connessione memorizzando i parametri di rete.
     * La connessione vera e propria verrà avviata chiamando il metodo {@link #run()}
     * (eseguito da un Thread).
     *
     * @param port La porta su cui il server è in ascolto.
     * @param ipAddress L'indirizzo IP del server.
     */
    public ConnectionHandler(int port, String ipAddress) {
        this.port = port;
        this.ipAddress = ipAddress;
    }

    /**
     * Imposta il listener (callback) che verrà invocato alla ricezione di ogni nuovo messaggio dal server.
     * @param currentListener Una funzione (Consumer) che definisce come elaborare il {@link Message} ricevuto.
     */
    public void setCurrentListener(Consumer<Message> currentListener) {
        this.currentListener = currentListener;
    }

    /**
     * Il ciclo di vita principale del thread di rete. 
     * Si divide in due fasi:
     * 1. Tentativi multipli (massimo 5) di stabilire una connessione con il server.
     * 2. Se connesso, ciclo infinito di attesa e lettura dei messaggi in ingresso dal server.
     */
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

    /**
     * Metodo di appoggio per delegare l'elaborazione del messaggio in ingresso al listener attuale.
     *
     * @param message Il messaggio appena letto dallo stream di rete.
     */
    private void handleMessage(Message message) {
        currentListener.accept(message);
    }

    /**
     * Invia un messaggio al server scrivendolo in modo sincrono sull'{@link ObjectOutputStream}.
     *
     * @param message L'oggetto di tipo {@link Message} da inviare.
     * @throws IOException In caso di problemi di scrittura sul socket (es. server disconnesso).
     */
    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }


    /**
     * Richiede la chiusura pulita e sicura della connessione di rete.
     * Ferma il thread di ascolto, chiude gli stream I/O e infine chiude il socket.
     */
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