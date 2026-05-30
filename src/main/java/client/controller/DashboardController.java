package client.controller;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import server.model.database.UtenteDAO;
import shared.model.UtenteLogin;

public class DashboardController {

    @FXML
    private Button StartButton;

    @FXML
    private ListView<String> listadash;


    @FXML
    private void initialize(){

    }

    @FXML
    private void iniziaPartita(){
        System.out.println("la partita è iniziata");
    }


}
