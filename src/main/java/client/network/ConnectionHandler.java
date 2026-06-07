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

public class ConnectionHandler implements Runnable{

    private Socket socket;
    private int port;
    private String ipAddress;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Consumer<Message> currentListener;


    public ConnectionHandler(int port, String ipAddress){
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public void setCurrentListener(Consumer<Message> currentListener) {
        this.currentListener = currentListener;
    }

    @Override
    public void run() {
        for (int tentativi = 0; tentativi < 5; tentativi++) {
            try {
                this.socket = new Socket(ipAddress, port);


                this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                this.out.flush();
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                System.out.println("Connessione al server riuscita on ip: " + socket.getInetAddress() + "  port: " + socket.getPort());
                while (true) {
                    handleMessage((Message) in.readObject());
                }
            } catch (IOException e) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Riavvia l'app");
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleMessage(Message message){
        currentListener.accept(message);
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }


}
