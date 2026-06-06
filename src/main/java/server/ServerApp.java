package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.model.database.DatabaseManager;

import java.io.IOException;

public class ServerApp extends Application {

    // Placeholder per la futura classe che gestirà il Server Socket
    //private SocketServer socketServer;

    @Override
    public void init() throws Exception {
        System.out.println("Inizializzazione risorse pre-avvio in corso...");
        
        // TODO: Leggere il file server.properties

        
        DatabaseManager.inizializzaDatabase();

        // TODO: Daemon per le socket

        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/serverLogin.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Gestionale Guess The Word");
        primaryStage.setScene(scene);
        primaryStage.show();

        // TODO: Inizializzare e avviare il thread del server in ascolto sulle connessioni.

        // socketServer = new SocketServer(5000); // la porta andrà letta da file properties

    }

    @Override
    public void stop() throws Exception {
        // Questo metodo viene chiamato automaticamente alla chiusura della finestra dell'app.
        System.out.println("Chiusura del server e rilascio delle risorse in corso...");
        
        // TODO: Arrestare il thread del Server Socket e i vari Client connessi
        // if (socketServer != null) {
        //     socketServer.stopServer();
        // }
        
        // TODO: Aggiungere un metodo al DatabaseManager per chiudere la connessione al DB
        
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}
