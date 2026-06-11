package server.model.service;

import server.model.database.*;
import server.model.database.entity.UtenteEntity;
import shared.game.AnalisiTesto;
import shared.game.Documento;
import shared.game.Statistica;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ServerDashboardService {

    private final DocumentoDAO documentoDAO;
    private final AnalisiTestoDAO analisiTestoDAO;
    private final StatisticaDAO statisticaDAO;

    public ServerDashboardService() {
        this.documentoDAO = new DocumentoDAO();
        this.analisiTestoDAO = new AnalisiTestoDAO();
        this.statisticaDAO = new StatisticaDAO();
    }

    public List<String> getNomiDocumenti() throws Exception {
        List<Documento> documenti = documentoDAO.elencaTutti();
        List<String> nomi = new ArrayList<>();
        for (Documento doc : documenti) {
            nomi.add(doc.getNome() + " (Analizzato)");
        }
        return nomi;
    }

    public void analizzaFile(List<File> files) throws Exception {
        for (File file : files) {
            String testo = new String(Files.readAllBytes(file.toPath()), "UTF-8");

            Documento documento = new Documento(0, file.getName(), testo);
            documentoDAO.aggiungi(documento);

            AnalisiTesto analisiTesto = new AnalisiTesto(documento.getIdDocumento());
            analisiTesto.analizza(testo);
            analisiTestoDAO.aggiungi(analisiTesto);
        }
    }

    public void eliminaDocumenti(List<String> filesSelected) throws Exception {
        List<Documento> tuttiIDocumenti = documentoDAO.elencaTutti();

        for (String stringaSelezionata : filesSelected) {
            for (Documento doc : tuttiIDocumenti) {
                String formatoStandard = doc.getNome() + " (Analizzato)";
                String formatoBackup = "Doc ID: " + doc.getIdDocumento() + " (Ripristinato da file)";

                if (stringaSelezionata.equals(formatoStandard) || stringaSelezionata.equals(formatoBackup)) {
                    documentoDAO.rimuovi(doc);
                    break;
                }
            }
        }
    }

    public List<Statistica> getClassifica() throws Exception {
        return statisticaDAO.elencaTutti();
    }

    public List<String> getListaUsernameUtenti() throws Exception {
        UtenteDAO utenteDAO = new UtenteDAO();
        List<UtenteEntity> utenti = utenteDAO.elencaTutti();
        List<String> usernames = new ArrayList<>();
        for (UtenteEntity u : utenti) {
            usernames.add(u.getUsername());
        }
        return usernames;
    }

    public void eliminaUtente(String username) throws Exception{
        UtenteDAO utenteDAO = new UtenteDAO();
        UtenteEntity utente = utenteDAO.cerca(username);

        if(utente != null){
            utenteDAO.rimuovi(utente);
        }
    }

    public void esportaDatabase(File fileDestinazione) throws Exception {
        DatabaseManager.eseguiBackup(fileDestinazione.getAbsolutePath());
    }

    public void ripristinaDatabase(File fileSorgente) throws Exception {
        DatabaseManager.eseguiRestore(fileSorgente.getAbsolutePath());
    }
}