package server.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import server.model.database.DatabaseManager;
import server.model.service.ServerDashboardService;
import shared.game.Statistica;
import shared.gui.util.SceneManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerDashboardController {

    @FXML
    private ListView<String> listDocuments;

    @FXML
    private Button loadTextButton;

    @FXML
    private Button analyzeTextButton;

    @FXML
    private Button backupButton;

    @FXML
    private Button restoreButton;

    @FXML
    private Button deleteTextButton;

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

    private List<File> filesSelected = new ArrayList<>();
    private final ServerDashboardService serverService = new ServerDashboardService();

    @FXML
    public void initialize(){
        configureStatsTable();
        serverStatusLabel.setText("In ascolto... (Pronto)");
        analyzeTextButton.setDisable(true);

        listDocuments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        if(deleteTextButton != null){
            deleteTextButton.disableProperty().bind(
                    Bindings.isEmpty(listDocuments.getSelectionModel().getSelectedItems())
            );
            caricaDatiDatabase();
        }
    }

    private void caricaDatiDatabase(){
        aggiornaStato("Sincronizzazione con il Database in corso...");
        Task<List<String>> loadTask = new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                return serverService.getNomiDocumenti();
            }
        };

        loadTask.setOnSucceeded(e -> {
            listDocuments.getItems().setAll(loadTask.getValue());
            aggiornaStato("Sincronizzazione completata");
        });

        loadTask.setOnFailed(e -> {
            loadTask.getException().printStackTrace();
            aggiornaStato("Errore nel caricamento dei dati iniziali");
        });

        startTask(loadTask);
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
                listDocuments.getItems().add(f.getName() + " (In attesa)");
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

        Task<Integer> taskAnalisi = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return serverService.analizzaFile(filesSelected);
            }
        };

        taskAnalisi.setOnSucceeded(e->{
            aggiornaStato("Analisi completata");
            filesSelected.clear();
            analyzeTextButton.setDisable(true);
            loadTextButton.setDisable(false);
            showAlert(Alert.AlertType.INFORMATION, "Analisi Completata", "I file selezionati sono stati analizzati con successo");
        });

        taskAnalisi.setOnFailed(e->{
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
                    DatabaseManager.eseguiRestore(fileSelected.getAbsolutePath());
                    return null;
                }
            };

            taskRestore.setOnSucceeded(e->{
                aggiornaStato("Restore Completato con successo");
                restoreButton.setDisable(false);

                caricaDatiDatabase();
                aggiornaStatistiche();

                showAlert(Alert.AlertType.INFORMATION, "Restore Completato", "Il database è satto ripristinato con successo");
            });

            taskRestore.setOnFailed(e->{
                taskRestore.getException().printStackTrace();
                aggiornaStato("Errore durante il restore");
                restoreButton.setDisable(false);
                showAlert(Alert.AlertType.ERROR, "Errore Database", "Impossibile ripristinare il file");
            });

            Thread thread = new Thread(taskRestore);
            thread.setDaemon(true);
            thread.start();
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

            Task<Integer> deleteTask = new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    return serverService.eliminaDocumenti(filesSelected);
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

        refreshStatsButton.setDisable(true);
        serverStatusLabel.setText("Estrazione statistiche dal DB in corso...");

        Task<List<Statistica>> loadStatsTask = new Task<List<Statistica>>() {
            @Override
            protected List<Statistica> call() throws Exception {
                return serverService.getClassifica();
            }
        };

        loadStatsTask.setOnSucceeded(e->{
            List<Statistica> stats = loadStatsTask.getValue();

            if(stats != null && !stats.isEmpty()){
                statsTableView.setItems(FXCollections.observableArrayList(stats));
                serverStatusLabel.setText("Statistiche aggiornate con successo.");
            } else {
                statsTableView.getItems().clear();
                serverStatusLabel.setText("Nessuna statistica presente nel DB.");
            }

            refreshStatsButton.setDisable(false);
        });

        loadStatsTask.setOnFailed(e->{
            loadStatsTask.getException().printStackTrace();
            serverStatusLabel.setText("Errore nel caricamento delle statistiche del Database-");
            refreshStatsButton.setDisable(false);
        });

        new Thread(loadStatsTask).start();
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

