package server.model.network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class NetworkConnection {
    private String ip;
    private int port;
    private ConnectionThread connection;
    private Consumer<Serializable> onReceive;


    public NetworkConnection(Consumer<Serializable> onReceive) {
        this.onReceive = onReceive;

        // TODO: Lettura di IP e porta dal file di configurazione con java properties


    }

    public void connect() {

        connection.start();

    }

    public void disconnect() throws IOException {

        connection.s.close();

    }

    public void send(Serializable data) throws IOException {

        connection.oos.writeObject(data);

    }

    /*
    private void handleMessage(Serializable msg) {


        System.out.println(msg);


    } */

    public boolean isServer() {
        return false;
    }


    private class ConnectionThread extends Thread  {

        Socket s;
        ObjectOutputStream oos;

        @Override
        public void run()
        {


            try(Socket s = isServer() ? new ServerSocket(port).accept() : new Socket(ip,port);

                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

            ) {

                this.s = s;
                this.oos = oos;


                while(true) {

                    Serializable msg = (Serializable) ois.readObject();

                    onReceive.accept(msg);

                    // handleMessage(msg);


                }




            } catch (IOException ex) {

                System.out.println("Connessione chiusa.");

            } catch (ClassNotFoundException ex) {
               System.out.println("arrivata classe capocchiosa");
            }

        }



    }



}
