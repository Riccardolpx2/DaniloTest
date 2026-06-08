package client;

import client.network.ConnectionHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
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
    public  void init() throws Exception{

        instance = this;

        Properties prop = new Properties();
        String filename = "client.properties";

        try (InputStream input = ClientApp.class.getClassLoader().getResourceAsStream(filename)) {

            if (input == null) {
                System.out.println("Il file di configurazione non è stato trovato " + filename);
                return;
            }

            prop.load(input);
            port=prop.getProperty("server.port");
            ipAddress =prop.getProperty("server.ip");
            vers=prop.getProperty("version");



            System.out.println("Avvio connessione verso :" + ipAddress + ":"+port+" version "+vers);


        } catch (IOException ex) {
            System.err.println("errore lettura file configurazione");
        }

        connectionHandler=new ConnectionHandler(Integer.parseInt(port), ipAddress);

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

        System.out.println("Chiusura del cient e relative risorse in corso attendere...");

        if(connectionHandlerThread != null){
            connectionHandlerThread.interrupt();
        }


        super.stop();
    }


    public static void main(String[] args) {
        launch();
    }
}
