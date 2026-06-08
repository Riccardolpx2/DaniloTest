package server.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import server.model.database.AmministratoreDAO;
import server.model.database.entity.AmministratoreEntity;
import server.model.service.AuthService;
import shared.gui.util.SceneManager;

public class ServerLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private AuthService authService;

    @FXML
    private void initialize(){
        this.authService = new AuthService();


        loginButton.disableProperty().bind(
                usernameField.textProperty().isEmpty().or(
                        passwordField.textProperty().isEmpty()
                ));
    }


    @FXML
    private void login(ActionEvent event){
        String username = usernameField.getText();
        String password = passwordField.getText();

        Task<AmministratoreEntity> loginTask = new Task<AmministratoreEntity>() {
            @Override
            protected AmministratoreEntity call() throws Exception {
                return authService.loginAdmin(username, password);
            }
        };

        loginTask.setOnSucceeded(e->{
            AmministratoreEntity admin = loginTask.getValue();
            if(admin != null){
                Stage stage = SceneManager.getStageFromEvent(event);
                SceneManager.switchScene(stage, "/fxml/server/serverDashboard.fxml");
            } else {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login fallito");
                alert.setHeaderText(null);
                alert.setContentText("Login fallito: Username o Password sbagliati");
                alert.showAndWait();
            }
        });

        loginTask.setOnFailed(e->{
            loginTask.getException().printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di Sistema");
            alert.setHeaderText(null);
            alert.setContentText("Impossibile connettersi al database.");
            alert.showAndWait();
        });

        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();

        // Da completare con il metodo per inviare al server
    }

//    @FXML
//    private void register(ActionEvent event){
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/clientRegister.fxml"));
//            Parent root = loader.load();
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }
}
