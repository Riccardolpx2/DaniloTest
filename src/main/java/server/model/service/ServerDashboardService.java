package server.model.service;

import server.model.database.*;
import server.model.database.entity.UtenteEntity;
import server.gameUtil.AnalisiTesto;
import server.gameUtil.Documento;
import server.gameUtil.Statistica;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce le operazioni principali della dashboard del server.
 * Fa da tramite tra l'interfaccia grafica e il database, occupandosi di recuperare,
 * salvare o eliminare le informazioni necessarie per il funzionamento del gioco.
 */
public class ServerDashboardService {

    private final DocumentoDAO documentoDAO;
    private final AnalisiTestoDAO analisiTestoDAO;
    private final StatisticaDAO statisticaDAO;

    /**
     * Costruttore: prepara i collegamenti necessari per poter leggere
     * e scrivere le informazioni all'interno del database.
     */
    public ServerDashboardService() {
        this.documentoDAO = new DocumentoDAO();
        this.analisiTestoDAO = new AnalisiTestoDAO();
        this.statisticaDAO = new StatisticaDAO();
    }

    /**
     * Recupera dal database i nomi di tutti i documenti già presenti nel sistema
     * e li prepara per essere mostrati a schermo.
     * @return Una lista contenente i nomi dei documenti pronti per la visualizzazione.
     * @throws Exception In caso di problemi di comunicazione con il database.
     */
    public List<String> getNomiDocumenti() throws Exception {
        List<Documento> documenti = documentoDAO.elencaTutti();
        List<String> nomi = new ArrayList<>();
        for (Documento doc : documenti) {
            nomi.add(doc.getNome() + " (Analizzato)");
        }
        return nomi;
    }

    /**
     * Prende i file di testo caricati dall'amministratore, ne legge il contenuto,
     * li analizza per il gioco e salva tutti i risultati in modo permanente.
     * @param files La lista dei file scelti dal computer dell'amministratore.
     * @throws Exception Se c'è un errore nella lettura dei file o nel salvataggio.
     */
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

    /**
     * Cerca i documenti indicati dall'amministratore e li elimina definitivamente dal sistema.
     * @param filesSelected I nomi dei documenti che si vogliono cancellare.
     * @throws Exception In caso di problemi durante l'eliminazione.
     */
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

    /**
     * Recupera la classifica completa di tutti i giocatori registrati,
     * includendo le loro vittorie, sconfitte e i tempi di risposta.
     * @return La lista contenente le statistiche di tutti i giocatori.
     * @throws Exception In caso di problemi di comunicazione con il database.
     */
    public List<Statistica> getClassifica() throws Exception {
        return statisticaDAO.elencaTutti();
    }

    /**
     * Recupera l'elenco di tutti gli utenti registrati al gioco.
     * @return Una lista contenente solo i nomi utente (username) dei giocatori.
     * @throws Exception In caso di errori durante la lettura dei dati.
     */
    public List<String> getListaUsernameUtenti() throws Exception {
        UtenteDAO utenteDAO = new UtenteDAO();
        List<UtenteEntity> utenti = utenteDAO.elencaTutti();
        List<String> usernames = new ArrayList<>();
        for (UtenteEntity u : utenti) {
            usernames.add(u.getUsername());
        }
        return usernames;
    }

    /**
     * Cerca un utente specifico tramite il suo nome e lo elimina per sempre dal sistema.
     * @param username Il nome dell'utente che si desidera cancellare.
     * @throws Exception Se ci sono problemi nell'eliminazione.
     */
    public void eliminaUtente(String username) throws Exception{
        UtenteDAO utenteDAO = new UtenteDAO();
        UtenteEntity utente = utenteDAO.cerca(username);

        if(utente != null){
            utenteDAO.rimuovi(utente);
        }
    }

    /**
     * Crea un file di sicurezza contenente una copia esatta di tutte le informazioni
     * attualmente presenti nel gioco (utenti, documenti, statistiche).
     * @param fileDestinazione Il file sul computer dove verrà salvata la copia.
     * @throws Exception Se c'è un errore durante la creazione del salvataggio.
     */
    public void esportaDatabase(File fileDestinazione) throws Exception {
        DatabaseManager.eseguiBackup(fileDestinazione.getAbsolutePath());
    }

    /**
     * Prende un file di salvataggio creato in precedenza e lo sostituisce ai dati attuali,
     * riportando il gioco esattamente a come era in quel momento.
     * @param fileSorgente Il file contenente il vecchio salvataggio da caricare.
     * @throws Exception Se il file è danneggiato o ci sono problemi nel ripristino.
     */
    public void ripristinaDatabase(File fileSorgente) throws Exception {
        DatabaseManager.eseguiRestore(fileSorgente.getAbsolutePath());
    }
}