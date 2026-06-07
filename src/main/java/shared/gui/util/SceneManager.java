package shared.gui.util;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    public static Stage getStageFromEvent(Event event){
        return (Stage) ((Node)event.getSource()).getScene().getWindow();
    }

    public static void switchScene(Event event, String fxmlPath){
        Stage stage = getStageFromEvent(event);
        switchScene(stage, fxmlPath);
    }

    public static void switchScene(Stage stage, String fxmlPath){
        try{
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
