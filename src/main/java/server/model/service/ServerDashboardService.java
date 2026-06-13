package server.model.service;

import server.model.database.*;
import server.model.database.entity.UtenteEntity;
import server.gameUtil.AnalisiTesto;
import server.gameUtil.Documento;
import server.gameUtil.Statistica;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import server.gameLogic.GeneratoreDomanda;
import server.gameUtil.Domanda;

public class ServerDashboardService {

    private final DocumentoDAO documentoDAO;
    private final AnalisiTestoDAO analisiTestoDAO;
    private final StatisticaDAO statisticaDAO;
    private final DomandaDAO domandaDAO;
    public ServerDashboardService() {
        this.documentoDAO = new DocumentoDAO();
        this.analisiTestoDAO = new AnalisiTestoDAO();
        this.statisticaDAO = new StatisticaDAO();
        this.domandaDAO = new DomandaDAO();
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
            
            int domandePerDifficolta = 5; 
            
            List<String> difficoltaDisponibili = Arrays.asList("FACILE", "MEDIA", "DIFFICILE");
            GeneratoreDomanda generatoreDomanda = new GeneratoreDomanda();

            // Il ciclo genera 20 domande per Facile, 20 per Media, 20 per Difficile (60 totali per libro)
            for (String diff : difficoltaDisponibili) {
                
                // Richiama l'algoritmo crittografico passandogli il set di lemmi appena calcolato
                List<Domanda> domandeGenerate = generatoreDomanda.creaDomande(domandePerDifficolta, diff, documento, analisiTesto);
                
                // Salva le domande una a una nel database per renderle pronte all'uso futuro
                if (domandeGenerate != null) {
                    for (Domanda d : domandeGenerate) {
                        domandaDAO.aggiungi(d);
                    }
                }
            }
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