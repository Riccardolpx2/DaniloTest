package server.model.network;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Si occupa di rimanere in ascolto su una specifica porta TCP per accettare
 * nuove connessioni in ingresso dai client. Quando arriva una connessione,
 * avvia un nuovo thread di gestione ({@link ClientHandler}).
 */
public class ServerListener implements Runnable{
    private final ServerSocket serverSocket;

    /**
     * Crea un nuovo listener sulla porta specificata.
     *
     * @param port La porta su cui mettere in ascolto il server.
     * @throws IOException Se si verifica un errore durante la creazione del ServerSocket.
     */
    public ServerListener(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    /**
     * Esegue il ciclo infinito di accettazione delle connessioni.
     * Per ogni connessione accettata, avvia un thread demone separato per la gestione.
     */
    @Override
    public void run() {
        Socket socket;
        while(true){
            try {
                socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.setDaemon(true);
                thread.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
