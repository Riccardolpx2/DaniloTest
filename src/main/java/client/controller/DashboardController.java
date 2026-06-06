package client.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

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

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("la partita è iniziata");
    }


}
