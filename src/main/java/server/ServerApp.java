package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.model.database.DatabaseManager;
import server.model.network.ServerListener;
import server.model.network.state.ClientState;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerApp extends Application {

    // Placeholder per la futura classe che gestirà il Server Socket
    //private SocketServer socketServer;
    Thread serverThread;

    @Override
    public void init() throws Exception {
        System.out.println("Inizializzazione risorse pre-avvio in corso...");

        Properties prop = new Properties();
        String port = "9090";//default se non letto
        String ip_addr;
        String vers;
        /*
        Lettura dei parametri di configurazione dal file .propetries
         */
        String filename = "server.properties";

        try (InputStream input = ServerApp.class.getClassLoader().getResourceAsStream(filename)) {

            if (input == null) {
                System.out.println("Il file di configurazione non è stato trovato " + filename);
                return;
            }

            prop.load(input);
            port=prop.getProperty("server.port");
            ip_addr=prop.getProperty("server.ip");
            vers=prop.getProperty("version");



            System.out.println("inizializzazione su :" + ip_addr + ":"+port+" version "+vers);


        } catch (IOException ex) {
            System.err.println("errore lettura file configurazione");
        }


        /*
        Iniziallizazione database, creazione table
         */
        DatabaseManager.inizializzaDatabase();

        // TODO: Daemon per le socket

        ServerListener serverListener = new ServerListener(Integer.parseInt(port));

        serverThread = new Thread(serverListener);
        serverThread.setDaemon(true);
        serverThread.start();

        ClientState.inizializzaRoutingServer();

        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/server/serverLogin.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Gestionale Guess The Word");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    @Override
    public void stop() throws Exception {
        // Questo metodo viene chiamato automaticamente alla chiusura della finestra dell'app.
        System.out.println("Chiusura del server e rilascio delle risorse in corso...");

        if(serverThread != null){
            serverThread.interrupt();
        }

        // TODO: Aggiungere un metodo al DatabaseManager per chiudere la connessione al DB


        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}
