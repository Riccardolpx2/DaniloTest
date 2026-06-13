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
import server.model.database.entity.AmministratoreEntity;
import server.model.service.AuthService;
import shared.gui.util.SceneManager;

/**
 * Gestisce la schermata iniziale di accesso (login) al server.
 * Si occupa di controllare le credenziali dell'amministratore per garantire
 * che solo il personale autorizzato possa accedere al pannello di controllo.
 */
public class ServerLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private AuthService authService;

    /**
     * Prepara la schermata non appena viene aperta.
     * Imposta i collegamenti iniziali e si assicura che il pulsante di accesso
     * rimanga bloccato finché non vengono inseriti sia il nome utente che la password.
     */
    @FXML
    private void initialize(){
        this.authService = new AuthService();


        loginButton.disableProperty().bind(
                usernameField.textProperty().isEmpty().or(
                        passwordField.textProperty().isEmpty()
                ));
    }


    /**
     * Raccoglie i dati inseriti nei campi di testo e verifica se corrispondono a un
     * amministratore registrato. Se i dati sono corretti, sblocca il sistema e apre
     * il pannello di controllo; altrimenti mostra un avviso di errore a schermo.
     * @param event L'evento scatenato dalla pressione del pulsante di accesso.
     */
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

    }
}
