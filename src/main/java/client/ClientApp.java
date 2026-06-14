package client;

import client.network.ConnectionHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ClientApp extends Application {

    Thread connectionHandlerThread;
    ConnectionHandler connectionHandler;
    String port;
    String ipAddress;
    String vers;
    private String currentUser;

    private static ClientApp instance;

    public static ClientApp getInstance() {
        return instance;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void init() throws Exception {
        instance = this;

        Properties prop = new Properties();

        Path configPath = Paths.get("properties", "client.properties");

        // Controlliamo preventivamente che il file esista
        if (!Files.exists(configPath)) {
            System.out.println("Il file di configurazione non è stato trovato nel percorso: " + configPath);
            return;
        }

        try (InputStream input = Files.newInputStream(configPath)) {

            prop.load(input);
            port = prop.getProperty("server.port");
            ipAddress = prop.getProperty("server.ip");
            vers = prop.getProperty("version");

            System.out.println("Avvio connessione verso :" + ipAddress + ":" + port + " version " + vers);

        } catch (IOException ex) {
            System.err.println("Errore durante la lettura del file di configurazione in " + configPath);
            ex.printStackTrace();
            return;
        }

        connectionHandler = new ConnectionHandler(Integer.parseInt(port), ipAddress);

        connectionHandlerThread = new Thread(connectionHandler);
        connectionHandlerThread.setDaemon(true);
        connectionHandlerThread.start();

        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/clientLogin.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Guess The Word");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /*
    Chiusura delle risorse impiegate dal client
     */
    @Override
    public void stop() throws Exception {

        System.out.println("Chiusura del client e relative risorse in corso, attendere...");

        if (connectionHandler != null) {
            try {
                connectionHandler.sendMessage(new Message(MessageType.LOGOUT_REQUEST, null));
            } catch (Exception e) {
                System.out.println("Impossibile contattare il server per il logout (connessione già interrotta).");
            }
            
            // Chiudiamo la socket sbloccando il thread in lettura
            connectionHandler.closeConnection();
        }

        if (connectionHandlerThread != null) {
            connectionHandlerThread.interrupt();
        }

        super.stop();
    }


    public static void main(String[] args) {
        launch();
    }
}
