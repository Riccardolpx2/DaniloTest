package client.network;

import shared.protocol.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable{

    private Socket socket;
    private int port;
    private String ipAddress;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Bisogna implementare lo stato
    private State currentState;

    public ConnectionHandler(int port, String ipAddress){
        this.port = port;
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        for (int tentativi = 0; tentativi < 5; tentativi++) {
            try {
                this.socket = new Socket(ipAddress, port);

                this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                this.out.flush();
                this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                break;
            } catch (IOException e) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }


    }
}
