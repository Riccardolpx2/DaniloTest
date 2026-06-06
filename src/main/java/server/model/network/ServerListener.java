package server.model.network;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Si mette in ascolto sulla porta, quando arriva una connessione avvia il thread di gestione
public class ServerListener implements Runnable{
    private final ServerSocket serverSocket;

    public ServerListener(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

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
