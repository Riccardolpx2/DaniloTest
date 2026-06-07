package server.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import server.model.database.StatisticaDAO;
import shared.game.Documento;
import shared.game.Statistica;
import shared.gui.util.SceneManager;

import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ServerDashboardController {

    @FXML
    private ListView<String> listDocuments;

    @FXML
    private Button loadTextButton;

    @FXML
    private Button analyzeTextButton;

    @FXML
    private Button saveSerializedButton;

    @FXML
    private Button loadSerializedButton;

    @FXML
    private Label statusAnalisiLabel;

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

    private File fileSelected = null;
    //private Map<String, Integer>

    @FXML
    public void initialize(){
        configureStatsTable();
        serverStatusLabel.setText("Stato server: In ascolto... (Pronto)");
    }

    @FXML
    private void loadFileText(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleziona il documento da analizzare");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("File di testo TXT", "*.txt"));
        Stage stage = SceneManager.getStageFromEvent(event);
        fileSelected = fc.showOpenDialog(stage);

        if(fileSelected != null){
            listDocuments.getItems().add(fileSelected.getName());
            statusAnalisiLabel.setText("Stato: File ' " + fileSelected.getName() + " ' caricato. Pronto per l'analisi");

        }
    }

    @FXML
    private void avviaAnalisiTask(){
        if(fileSelected == null){
            statusAnalisiLabel.setText("Stato: Errore. Nessun file caricato");
            return;
        }

        statusAnalisiLabel.setText("Stato: Analisi in corso...");
        analyzeTextButton.setDisable(true);

        //TODO
    }

    @FXML
    private void salvaDatiSerializzati(){
        statusAnalisiLabel.setText("Stato: ");
    }

    @FXML
    private void caricaDatiSerializzati(){
        statusAnalisiLabel.setText("Stato: ");
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
    }

    @FXML
    private void aggiornaStatistiche(){

        refreshStatsButton.setDisable(true);
        serverStatusLabel.setText("Stato Server: Estrazione statistiche dal DB in corso...");

        Task<List<Statistica>> loadStatsTask = new Task<List<Statistica>>() {
            @Override
            protected List<Statistica> call() throws Exception {
                StatisticaDAO statDao = new StatisticaDAO();
                return statDao.elencaTutti();
            }
        };
        loadStatsTask.setOnSucceeded(e->{
            List<Statistica> stats = loadStatsTask.getValue();

            if(stats != null && !stats.isEmpty()){
                statsTableView.setItems(FXCollections.observableArrayList(stats));
                serverStatusLabel.setText("Stato Server: Statistiche aggiornate con successo.");
            } else {
                statsTableView.getItems().clear();
                serverStatusLabel.setText("Stato Server: Nessuna statistica presente nel DB.");
            }

            refreshStatsButton.setDisable(false);
        });

        loadStatsTask.setOnFailed(e->{
            loadStatsTask.getException().printStackTrace();
            serverStatusLabel.setText("Stato Server: Errore nel caricamento delle statistiche del Database-");
            refreshStatsButton.setDisable(false);
        });

        new Thread(loadStatsTask).start();
    }


}

