package server.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import server.model.service.ServerDashboardService;
import shared.game.Statistica;
import shared.gui.util.SceneManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServerDashboardController {

    @FXML
    private TextField searchDocumentField;

    @FXML
    private ListView<String> listDocuments;

    @FXML
    private Button deleteTextButton;

    @FXML
    private ListView<String> listNewDocuments;

    @FXML
    private Button loadTextButton;

    @FXML
    private Button analyzeTextButton;

    @FXML
    private Button backupButton;

    @FXML
    private Button restoreButton;

    @FXML
    private TextField searchStatsField;

    @FXML
    private TableView<Statistica> statsTableView;

    @FXML
    private TableColumn<Statistica, String> userCol;

    @FXML
    private TableColumn<Statistica, Integer> winsCol;

    @FXML
    private TableColumn<Statistica, Integer> matchesCol;

    @FXML
    private TableColumn<Statistica, Double> avgTimeCol;

    @FXML
    private Button refreshStatsButton;

    @FXML
    private Label serverStatusLabel;

    @FXML
    private ListView<String> listUsers;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button refreshUserButton;

    private List<File> filesSelected = new ArrayList<>();
    private List<String> allDocumentsList = new ArrayList<>();
    private List<Statistica> allStatsList = new ArrayList<>();
    private final ServerDashboardService serverService = new ServerDashboardService();

    @FXML
    public void initialize(){
        configureStatsTable();
        aggiornaStato("In ascolto.... (Pronto)");

        analyzeTextButton.setDisable(true);

        if(listDocuments != null){
            listDocuments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        }

        if(listUsers != null){
            listUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }

        if(listNewDocuments != null){
            listNewDocuments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }

        if(searchDocumentField != null){
            searchDocumentField.textProperty().addListener((observable, oldValue, newValue) -> {
                filtraDocumenti(newValue);
            });
        }

        if(searchStatsField != null){
            searchStatsField.textProperty().addListener((observable, oldValue, newValue) -> {
                filtraStatistiche(newValue);
            });
        }

        if(deleteTextButton != null) {
            deleteTextButton.disableProperty().bind(
                    Bindings.isEmpty(listDocuments.getSelectionModel().getSelectedItems())
            );
        }

        if(deleteUserButton != null) {
            deleteUserButton.disableProperty().bind(
                    Bindings.isEmpty(listUsers.getSelectionModel().getSelectedItems())
            );
        }

        aggiornaDocumenti();
        aggiornaUtenti();
    }

    private void filtraDocumenti(String filtro){
        if(filtro == null || filtro.trim().isEmpty()){
            listDocuments.getItems().setAll(allDocumentsList);
        } else {
            List<String> filtrati = allDocumentsList.stream()
                    .filter(doc->doc.toLowerCase().contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
            listDocuments.getItems().setAll(filtrati);
        }
    }

    private void filtraStatistiche(String filtro){
        if(filtro == null || filtro.trim().isEmpty()){
            statsTableView.getItems().setAll(allStatsList);
        } else {
            List<Statistica> filtrati = allStatsList.stream()
                    .filter(stat -> stat.getPlayer().getUsername().toLowerCase().contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    private void aggiornaDocumenti(){
        aggiornaStato("Sincronizzazione con il Database in corso...");
        Task<List<String>> loadTask = new Task<List<String> >() {
            @Override
            protected List<String> call() throws Exception {
                return serverService.getNomiDocumenti();
            }
        };

        loadTask.setOnSucceeded(event -> {
            allDocumentsList = loadTask.getValue();

            filtraDocumenti(searchDocumentField != null ? searchDocumentField.getText() : "");
            aggiornaStato("Elenco documenti sincronizzato.");
        });

        loadTask.setOnFailed(event -> {
            aggiornaStato("Errore caricamento documenti: " + loadTask.getException().getMessage());
        });

        startTask(loadTask);
    }

    @FXML
    private void aggiornaUtenti(){
        if(refreshUserButton != null){
            refreshUserButton.setDisable(true);
        }

        Task<List<String>> loadUsersTask = new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                return serverService.getListaUsernameUtenti();
            }
        };

        loadUsersTask.setOnSucceeded(e->{
            List<String> users = loadUsersTask.getValue();
            if(users != null && !users.isEmpty()){
                listUsers.getItems().setAll(users);
                aggiornaStato("Lista utenti aggiornata con successo");
            } else {
                listUsers.getItems().clear();
                aggiornaStato("Nessuna utente presente nel DB");
            }

            if(refreshUserButton != null) refreshUserButton.setDisable(false);
        });

        loadUsersTask.setOnFailed(e->{
            aggiornaStato("Errore nel caricamento degli utenti dal Database: " + loadUsersTask.getException().getMessage());
            if(refreshUserButton != null) refreshUserButton.setDisable(false);
        });

        startTask(loadUsersTask);
    }


    @FXML
    private void loadFileText(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleziona il documento da analizzare");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("File di testo TXT", "*.txt"));
        Stage stage = SceneManager.getStageFromEvent(event);
        List<File> files = fc.showOpenMultipleDialog(stage);

        if(files != null && !files.isEmpty()){
            filesSelected.addAll(files);
            for (File f: files){
                listNewDocuments.getItems().add(f.getName() + " (In attesa)");
            }
            aggiornaStato( files.size() + " file pronti per l'analisi");
            analyzeTextButton.setDisable(false);
        }
    }

    @FXML
    private void avviaAnalisiTask(){
        if(filesSelected.isEmpty()) return;

        aggiornaStato("Analisi in corso...");
        analyzeTextButton.setDisable(true);
        loadTextButton.setDisable(true);

        Alert popupAttesa = new Alert(Alert.AlertType.INFORMATION);
        popupAttesa.setTitle("Elaborazione in corso");
        popupAttesa.setHeaderText("Analisi dei documenti...");
        popupAttesa.getDialogPane().getButtonTypes().clear();
        popupAttesa.show();

        Task<Void> taskAnalisi = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                serverService.analizzaFile(filesSelected);
                return null;
            }
        };

        taskAnalisi.setOnSucceeded(e->{
            popupAttesa.getDialogPane().getButtonTypes().add(ButtonType.OK);
            popupAttesa.close();

            aggiornaStato("Analisi completata");
            filesSelected.clear();

            if(listNewDocuments != null) listNewDocuments.getItems().clear();

            analyzeTextButton.setDisable(true);
            loadTextButton.setDisable(false);

            aggiornaDocumenti();

            showAlert(Alert.AlertType.INFORMATION, "Analisi Completata", "I file selezionati sono stati analizzati con successo");
        });

        taskAnalisi.setOnFailed(e->{

            popupAttesa.getDialogPane().getButtonTypes().add(ButtonType.OK);
            popupAttesa.close();

            aggiornaStato("Errore durante l'analisi");
            analyzeTextButton.setDisable(false);
            loadTextButton.setDisable(false);
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile analizzare i file: " + taskAnalisi.getException().getMessage());
        });

        startTask(taskAnalisi);
    }

    @FXML
    private void eseguiBackup(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("Esporta Database (.db)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("File SQLite (*.db)", "*.db"));
        fc.setInitialFileName("Backup_GuessTheWord.db");

        Stage stage = SceneManager.getStageFromEvent(event);
        File fileSelected = fc.showSaveDialog(stage);

        if(fileSelected != null){
            aggiornaStato("Esecuzione backup in corso...");
            backupButton.setDisable(true);

            Task<Void> taskBackup = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    serverService.esportaDatabase(fileSelected);
                    return null;
                }
            };

            taskBackup.setOnSucceeded(e->{
                aggiornaStato("Backup completato con successo.");
                backupButton.setDisable(false);
                showAlert(Alert.AlertType.INFORMATION, "Backup Completato", "Esportazione completata.");
            });

            taskBackup.setOnFailed(e->{
                aggiornaStato("Errore durante il backup.");
                backupButton.setDisable(false);
                showAlert(Alert.AlertType.ERROR, "Errore Database", "Impossibile eseguire l'esportazione: " + taskBackup.getException().getMessage());
            });

            startTask(taskBackup);
        }
    }

    @FXML
    private void restoreBackup(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("Restore del database");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("File SQLite (*.db)", "*.db"));

        Stage stage = SceneManager.getStageFromEvent(event);
        File fileSelected = fc.showOpenDialog(stage);

        if(fileSelected != null){
            aggiornaStato("Restore del database in corso...");
            restoreButton.setDisable(true);

            Task<Void> taskRestore = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    serverService.ripristinaDatabase(fileSelected);
                    return null;
                }
            };

            taskRestore.setOnSucceeded(e->{
                aggiornaStato("Restore Completato con successo");
                restoreButton.setDisable(false);

                aggiornaDocumenti();
                aggiornaUtenti();
                aggiornaStatistiche();

                showAlert(Alert.AlertType.INFORMATION, "Restore Completato", "Il database è satto ripristinato con successo");
            });

            taskRestore.setOnFailed(e->{
                aggiornaStato("Errore durante il restore: " + taskRestore.getException().getMessage());
                restoreButton.setDisable(false);
                showAlert(Alert.AlertType.ERROR, "Errore Database", "Impossibile ripristinare il file");
            });

            startTask(taskRestore);
        }
    }

    @FXML
    private void eliminaDocumenti(ActionEvent event){
        List<String> filesSelected = new ArrayList<>(listDocuments.getSelectionModel().getSelectedItems());
        if(filesSelected.isEmpty()){
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setContentText("Sei sicuro di voler eliminare definitivamente questi documenti? \n\n" +
                "ATTENZIONE: Questa operazione eliminerà anche le analisi e la cronologia delle partite associate a questi file");
        alert.setHeaderText(null);
        Optional<ButtonType> risultato = alert.showAndWait();

        if(risultato.isPresent() && risultato.get() == ButtonType.OK){
            aggiornaStato("Eliminazione dal database in corso...");

            Task<Void> deleteTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    serverService.eliminaDocumenti(filesSelected);
                    return null;
                }
            };

            deleteTask.setOnSucceeded(e->{
                listDocuments.getItems().removeAll(filesSelected);
                aggiornaStato("Documenti rimossi con successo");
                showAlert(Alert.AlertType.INFORMATION, "Eliminazione Completata", "I documenti sono stati rimossi con successo");
                aggiornaStatistiche();
            });

            deleteTask.setOnFailed(e->{
                aggiornaStato("Errore durante l'eliminazione");
                showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile completare l'operazione: " + deleteTask.getException().getMessage());
            });

            startTask(deleteTask);
        }
    }

    @FXML
    private void eliminaUtentiSelezionati(ActionEvent event){
        List<String> utentiSelected = new ArrayList<>(listUsers.getSelectionModel().getSelectedItems());
        if(utentiSelected.isEmpty()) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione Utenti");
        alert.setContentText("Sei sicuro di voler elimiare definitivamente gli utenti selezionati?\n\n" +
                "Verranno rimosse anche tutte le loro statistiche e cronologie di gioco associate.");
        alert.setHeaderText(null);

        Optional<ButtonType> risultato = alert.showAndWait();

        if(risultato.isPresent() && risultato.get() == ButtonType.OK){
            aggiornaStato("Eliminazione account utenti in corso...");

            Task<Void> deleteUsersTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    for(String username : utentiSelected){
                        serverService.eliminaUtente(username);
                    }
                    return null;
                }
            };

            deleteUsersTask.setOnSucceeded(e->{
                aggiornaStato("Account utenti rimossi con successo");
                listUsers.getItems().removeAll(utentiSelected);
                aggiornaStatistiche();
                showAlert(Alert.AlertType.INFORMATION, "Eliminazione Completata", "Gli utenti selezionati sono stati rimossi con successo");
            });

            deleteUsersTask.setOnFailed(e->{
                aggiornaStato("Errore durante l'eliminazione degli utenti");
                showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile completare l'operazione");
            });

            startTask(deleteUsersTask);
        }
    }

    @FXML
    private void configureStatsTable(){
        userCol.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getPlayer().getUsername()));
        winsCol.setCellValueFactory(cellData->new SimpleIntegerProperty(cellData.getValue().getVittorie()).asObject());
        matchesCol.setCellValueFactory(cellData->{
            int totali = cellData.getValue().getVittorie() + cellData.getValue().getSconfitte();
            return new SimpleIntegerProperty(totali).asObject();
        });
        avgTimeCol.setCellValueFactory(cellData->new SimpleDoubleProperty(cellData.getValue().getMediaRisposta()).asObject());
        aggiornaStatistiche();
    }

    @FXML
    private void aggiornaStatistiche(){

        if(refreshStatsButton!=null){
            refreshStatsButton.setDisable(true);
        }

        serverStatusLabel.setText("Estrazione statistiche dal DB in corso...");

        Task<List<Statistica>> loadStatsTask = new Task<List<Statistica>>() {
            @Override
            protected List<Statistica> call() throws Exception {
                return serverService.getClassifica();
            }
        };

        loadStatsTask.setOnSucceeded(e->{
            allStatsList = loadStatsTask.getValue();

            filtraStatistiche(searchStatsField != null ? searchStatsField.getText() : "");
            aggiornaStato("Statistiche aggiornate con successo");

            if(refreshStatsButton != null){
                refreshStatsButton.setDisable(false);
            }
        });

        loadStatsTask.setOnFailed(e->{
            aggiornaStato("Errore nel caricamento delle statistiche del Database: " +loadStatsTask.getException().getMessage());
            if(refreshStatsButton != null){
                refreshStatsButton.setDisable(false);
            }
        });

        startTask(loadStatsTask);
    }


    private void showAlert(Alert.AlertType tipo, String titolo, String messaggio) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void startTask(Task<?> task){
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void aggiornaStato(String messaggio){
        Platform.runLater(()->{
            serverStatusLabel.setText("Stato: " + messaggio);
        });
    }


}

