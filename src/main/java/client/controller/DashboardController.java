package client.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import shared.gui.util.SceneManager;

public class DashboardController {

    @FXML
    private Button StartButton;

    @FXML
    private ListView<String> listadash;


    @FXML
    private void initialize(){

    }

    @FXML
    private void iniziaPartita(ActionEvent event){
        System.out.println("la partita è iniziata");
        SceneManager.switchScene(event, "/fxml/client/game.fxml");
    }


    @FXML
    private void viewStatistiche(){
        // Da fare
    }

    @FXML
    private void iniziaPartita(){
        // anche questo da fare
    }

}
