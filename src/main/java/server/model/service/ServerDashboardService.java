package server.model.service;

import server.model.database.AnalisiTestoDAO;
import server.model.database.DatabaseManager;
import server.model.database.DocumentoDAO;
import server.model.database.StatisticaDAO;
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

    public int analizzaFile(List<File> files) throws Exception {
        int fileElaborati = 0;
        for (File file : files) {
            String testo = new String(Files.readAllBytes(file.toPath()), "UTF-8");

            Documento documento = new Documento(0, file.getName(), testo);
            documentoDAO.aggiungi(documento);

            AnalisiTesto analisiTesto = new AnalisiTesto(documento.getIdDocumento());
            analisiTesto.analizza(testo);
            analisiTestoDAO.aggiungi(analisiTesto);

            fileElaborati++;
        }
        return fileElaborati;
    }

    public int eliminaDocumenti(List<String> filesSelected) throws Exception {
        List<Documento> tuttiIDocumenti = documentoDAO.elencaTutti();
        int rimossi = 0;

        for (String stringaSelezionata : filesSelected) {
            for (Documento doc : tuttiIDocumenti) {
                String formatoStandard = doc.getNome() + " (Analizzato)";
                String formatoBackup = "Doc ID: " + doc.getIdDocumento() + " (Ripristinato da file)";

                if (stringaSelezionata.equals(formatoStandard) || stringaSelezionata.equals(formatoBackup)) {
                    documentoDAO.rimuovi(doc);
                    rimossi++;
                    break;
                }
            }
        }
        return rimossi;
    }

    public List<Statistica> getClassifica() throws Exception {
        return statisticaDAO.elencaTutti();
    }

    public void esportaDatabase(File fileDestinazione) throws Exception {
        DatabaseManager.eseguiBackup(fileDestinazione.getAbsolutePath());
    }

    public void ripristinaDatabase(File fileSorgente) throws Exception {
        DatabaseManager.eseguiRestore(fileSorgente.getAbsolutePath());
    }
}