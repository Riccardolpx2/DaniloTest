package client;

import client.network.ConnectionHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.ServerApp;
import server.model.database.DatabaseManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientApp extends Application {

    Thread connectionHandlerThread;
    ConnectionHandler connectionHandler;
    String port;
    String ip_addr;
    String vers;

    @Override
    public  void init() throws Exception{

        Properties prop = new Properties();
        String filename = "client.properties";

        try (InputStream input = ServerApp.class.getClassLoader().getResourceAsStream(filename)) {

            if (input == null) {
                System.out.println("Il file di configurazione non è stato trovato " + filename);
                return;
            }

            prop.load(input);
            port=prop.getProperty("server.port");
            ip_addr=prop.getProperty("server.ip");
            vers=prop.getProperty("version");



            System.out.println("Avvio connessione verso :" + ip_addr + ":"+port+" version "+vers);


        } catch (IOException ex) {
            System.err.println("errore lettura file configurazione");
        }

        connectionHandler=new ConnectionHandler(Integer.parseInt(port),ip_addr);

        connectionHandlerThread = new Thread(connectionHandler);
        connectionHandlerThread.setDaemon(true);
        connectionHandlerThread.start();

        super.init();

    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/clientLogin.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Guess The Word");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
