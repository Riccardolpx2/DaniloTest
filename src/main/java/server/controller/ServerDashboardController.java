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
import server.model.database.AnalisiTestoDAO;
import server.model.database.DocumentoDAO;
import server.model.database.StatisticaDAO;
import shared.game.AnalisiTesto;
import shared.game.Documento;
import shared.game.Statistica;
import shared.gui.util.SceneManager;

import java.io.*;
import java.nio.file.Files;
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
    private Button saveSerializedButton;

    @FXML
    private Button loadSerializedButton;

    @FXML
    private Button deleteTextButton;

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

    private List<File> filesSelected = new ArrayList<>();

    private List<AnalisiTesto> listaAnalisi = new ArrayList<>();


    @FXML
    public void initialize(){
        configureStatsTable();
        serverStatusLabel.setText("Stato server: In ascolto... (Pronto)");
        analyzeTextButton.setDisable(true);

        listDocuments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deleteTextButton.disableProperty().bind(
                Bindings.isEmpty(listDocuments.getSelectionModel().getSelectedItems())
        );
        caricaDocumentiDatabase();
    }

    private void caricaDocumentiDatabase(){
        statusAnalisiLabel.setText("Stato: Sincronizzazione con il Database in corso...");
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                DocumentoDAO documentoDAO = new DocumentoDAO();
                List<Documento> documenti = documentoDAO.elencaTutti();

                List<String> nomiDocumenti = new ArrayList<>();
                for(Documento doc : documenti){
                    nomiDocumenti.add(doc.getNome() + " (Analizzato)");
                }

                AnalisiTestoDAO analisiTestoDAO = new AnalisiTestoDAO();
                List<AnalisiTesto> analisi = analisiTestoDAO.elencaTutti();

                Platform.runLater(() -> {
                    listDocuments.getItems().setAll(nomiDocumenti);
                    listaAnalisi.clear();
                    listaAnalisi.addAll(analisi);
                });
                return null;
            }
        };

        loadTask.setOnSucceeded(e -> {
            statusAnalisiLabel.setText("Stato: Sincronizzazione completata");
        });

        loadTask.setOnFailed(e -> {
            loadTask.getException().printStackTrace();
            statusAnalisiLabel.setText("Stato: Errore nel caricamento dei dati iniziali");
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
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

            statusAnalisiLabel.setText("Stato: " + files.size() + " file pronti per l'analisi");
            analyzeTextButton.setDisable(false);
        }
    }

    @FXML
    private void avviaAnalisiTask(){
        if(filesSelected.isEmpty()) return;

        statusAnalisiLabel.setText("Stato: Analisi in corso...");
        analyzeTextButton.setDisable(true);
        loadTextButton.setDisable(true);

        Task<Integer> taskAnalisi = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                DocumentoDAO documentoDAO = new DocumentoDAO();
                AnalisiTestoDAO analisiTestoDAO = new AnalisiTestoDAO();
                int filesElaborati = 0;

                for(File file : filesSelected){
                    String testo = new String(Files.readAllBytes(file.toPath()), "UTF-8");

                    Documento documento = new Documento(0, file.getName(), testo);
                    documentoDAO.aggiungi(documento);

                    AnalisiTesto analisiTesto = new AnalisiTesto(documento.getIdDocumento());
                    analisiTesto.analizza(testo);

                    listaAnalisi.add(analisiTesto);
                    analisiTestoDAO.aggiungi(analisiTesto);

                    filesElaborati++;

                    final int count = filesElaborati;
                    final String oldOutput = file.getName() + " (In attesa)";
                    final String newOutput = file.getName() + " (Analizzato)";

                    Platform.runLater(() -> {
                        statusAnalisiLabel.setText("Stato: Elaborati " + count + " di " + filesSelected.size() + " file...");

                        int index = listDocuments.getItems().indexOf(oldOutput);
                        if(index != -1){
                            listDocuments.getItems().set(index, newOutput);
                        }
                    });
                }

                return filesElaborati;
            }
        };

        taskAnalisi.setOnSucceeded(e->{
            int elaborati = taskAnalisi.getValue();
            statusAnalisiLabel.setText("Stato: Analisi completata");

            filesSelected.clear();
            analyzeTextButton.setDisable(true);
            loadTextButton.setDisable(false);

            showAlert(Alert.AlertType.INFORMATION, "Analisi Completata", "I file selezionati sono stati analizzati con successo");
        });

        taskAnalisi.setOnFailed(e->{
            taskAnalisi.getException().printStackTrace();
            statusAnalisiLabel.setText("Stato: Errore durante l'analisi");
            analyzeTextButton.setDisable(false);
            loadTextButton.setDisable(false);
        });

        Thread th = new Thread(taskAnalisi);
        th.setDaemon(true);
        th.start();
    }

    @FXML
    private void salvaDatiSerializzati(ActionEvent event){
        if(listaAnalisi == null || listaAnalisi.isEmpty()){

            showAlert(Alert.AlertType.ERROR, "Errore Salvataggio", "Nessuna analisi presente");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Salva le analisi serializzate");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("File Dati Serializzati (*.dat)", "*.dat"));
        fc.setInitialFileName("analisi_backup.dat");

        Stage stage = SceneManager.getStageFromEvent(event);
        File fileSelected = fc.showSaveDialog(stage);

        if (fileSelected != null){
            statusAnalisiLabel.setText("Stato: Salvataggio nel file .dat in corso");
            saveSerializedButton.setDisable(true);

            Task<Void> taskSalvataggio = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileSelected))){
                        oos.writeObject(listaAnalisi);
                    }
                    return null;
                }
            };

            taskSalvataggio.setOnSucceeded(e->{
                statusAnalisiLabel.setText("Stato: File .dat salvato con successo");
                saveSerializedButton.setDisable(false);

                showAlert(Alert.AlertType.INFORMATION, "Salvataggio Completato", "Le analisi sono state salvate in:\n" + fileSelected.getName());
            });

            taskSalvataggio.setOnFailed(e->{
                taskSalvataggio.getException().printStackTrace();
                statusAnalisiLabel.setText("Stato: Errore durante la creazione del file .dat");
                saveSerializedButton.setDisable(false);
                showAlert(Alert.AlertType.ERROR, "Errore Salvataggio", "Nessuna analisi presente");
            });

            Thread thread = new Thread(taskSalvataggio);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    private void caricaDatiSerializzati(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("Carica un file di analisi");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("File Dati (*.dat)", "*.dat"));

        Stage stage = SceneManager.getStageFromEvent(event);
        File fileSelected = fc.showOpenDialog(stage);

        if(fileSelected != null){
            statusAnalisiLabel.setText("Stato: Lettura del file .dat in corso");
            loadSerializedButton.setDisable(true);

            Task<List<AnalisiTesto>> taskCaricamento = new Task<List<AnalisiTesto>>() {
                @Override
                protected List<AnalisiTesto> call() throws Exception {
                    try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileSelected))){
                        return (List<AnalisiTesto>) ois.readObject();
                    }
                }
            };

            taskCaricamento.setOnSucceeded(e->{
                listaAnalisi = taskCaricamento.getValue();
                loadSerializedButton.setDisable(false);

                statusAnalisiLabel.setText("Stato: Ripristinate anlisi dal file " + fileSelected.getName());

                showAlert(Alert.AlertType.INFORMATION, "Caricamento Completato", "Caricamento delle analisi avvenuto con successo");
            });

            taskCaricamento.setOnFailed(e->{
                taskCaricamento.getException().printStackTrace();
                statusAnalisiLabel.setText("Stato: File .dat non valido o corrotto");
                loadSerializedButton.setDisable(false);
                showAlert(Alert.AlertType.ERROR, "Errore di Caricamento", "Il file selezione non è valido o corrotto");
            });

            Thread thread = new Thread(taskCaricamento);
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
            statusAnalisiLabel.setText("Stato: Eliminazione dal database in corso...");

            Task<Void> deleteTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    DocumentoDAO documentoDAO = new DocumentoDAO();
                    List<Documento> documenti = documentoDAO.elencaTutti();

                    for(String s : filesSelected){
                        Documento docRemove = null;

                        for (Documento doc : documenti){
                            String docName = doc.getNome() + " (Analizzato)";
                            if (s.equals(docName)){
                                docRemove = doc;
                                break;
                            }
                        }

                        if (docRemove != null){
                            documentoDAO.rimuovi(docRemove);

                            final int idDoc = docRemove.getIdDocumento();
                            listaAnalisi.removeIf(analisi -> analisi.getIdDocumento() == idDoc);
                        }
                    }
                    return null;
                }
            };

            deleteTask.setOnSucceeded(e->{
                listDocuments.getItems().removeAll(filesSelected);
                statusAnalisiLabel.setText("Stato: Documenti rimossi con successo");
                showAlert(Alert.AlertType.INFORMATION, "Eliminazione Completata", "I documenti sono stati rimossi con successo");

                aggiornaStatistiche();
            });

            deleteTask.setOnFailed(e->{
                deleteTask.getException().printStackTrace();
                statusAnalisiLabel.setText("Stato: Errore durante l'eliminazione");
                showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile completare l'operazione: " + deleteTask.getException().getMessage());
            });

            Thread thread = new Thread(deleteTask);
            thread.setDaemon(true);
            thread.start();
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

    private void showAlert(Alert.AlertType tipo, String titolo, String messaggio) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }



}

