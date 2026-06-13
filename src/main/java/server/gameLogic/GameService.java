/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameLogic;

import java.sql.SQLException;
import java.util.List;
import server.model.database.PartitaDAO;
import server.model.database.StatisticaDAO;
import server.gameUtil.Partita;
import server.gameUtil.Statistica;
import server.model.database.entity.UtenteEntity;

/**
 * Servizio di logica applicativa dedicato alla gestione del ciclo post-partita e delle metriche dei giocatori.
 * Coordina le operazioni transazionali di salvataggio dei match e aggiorna in tempo reale 
 * lo storico e le medie ponderate delle performance (statistiche) dei singoli utenti coinvolti.
 * * @author Utente
 */
public class GameService {

private final PartitaDAO partitaDAO = new PartitaDAO();
private final StatisticaDAO statisticaDAO = new StatisticaDAO();

/**
     * Termina formalmente una partita, memorizzandone i dati e aggiornando i profili statistici dei giocatori.
     * L'operazione persiste la macro-partita (con relativi tempi dei round) e, in modo sequenziale, 
     * ricalcola e aggiorna i record storici di rendimento sia del Giocatore 1 che del Giocatore 2.
     * @param p L'oggetto {@link Partita} da storicizzare nel database.
     * @throws SQLException Se si verifica un errore durante il salvataggio della partita o dell'aggiornamento statistico.
     */
    public void terminaESalvaPartita(Partita p) throws SQLException {
        // 1. Salva la macro-partita e i relativi tempi dei round nel database
        partitaDAO.aggiungi(p); 
        
        // 2. Aggiorna o crea le statistiche per il Giocatore 1
        aggiornaStatisticheUtente(p.getPlayer1(), p.getTempiRispostaG1(), p.getVincitore());
        
        // 3. Aggiorna o crea le statistiche per il Giocatore 2
        aggiornaStatisticheUtente(p.getPlayer2(), p.getTempiRispostaG2(), p.getVincitore());
        
        System.out.println("Partita e statistiche aggiornate con successo nel DB.");
    }

    /**
     * Algoritmo interno di supporto per il ricalcolo analitico e la persistenza delle statistiche del singolo giocatore.
     * Se l'utente non possiede uno storico, viene generata una riga di baseline sul DB. Successivamente 
     * il metodo calcola la media dei tempi correnti, aggiorna la media storica ponderata, incrementa 
     * il contatore dei risultati (vittoria/sconfitta/pareggio) e aggiorna il rateo percentuale finale.
     * * @param giocatore        L'entità {@link UtenteEntity} del giocatore da aggiornare.
     * @param tempiPartita     La lista dei tempi di risposta (in secondi/millisecondi) accumulati nel match corrente.
     * @param vincitorePartita L'entità del vincitore del match (può essere null in caso di pareggio).
     * @throws SQLException Se si verificano anomalie durante le query di selezione, inserimento o aggiornamento.
     */
    private void aggiornaStatisticheUtente(UtenteEntity giocatore, List<Integer> tempiPartita, UtenteEntity vincitorePartita) throws SQLException {
        // Cerchiamo le statistiche usando lo username estratto dall'entità player
        Statistica stat = statisticaDAO.cerca(giocatore.getUsername());
        
        // Se l'utente non ha mai giocato (statistiche nulle), creiamo un nuovo record pulito
        if (stat == null) {
            // Usiamo il costruttore completo della tua classe Statistica (vittorie=0, sconfitte=0, %vittorie=0, media=0.0)
            stat = new Statistica(giocatore, 0, 0, 0, 0.0);
            statisticaDAO.aggiungi(stat);
        }

        // Calcolo della media dei tempi di questa partita
        double mediaTempiPartitaCorrente = tempiPartita.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(30.0); // Default a 30 secondi in caso di liste vuote

        // Calcolo dedlla nuova media ponderata storica
        int totalePartitePrecedenti = stat.getVittorie() + stat.getSconfitte();
        double nuovaMediaStorica;
        
        if (totalePartitePrecedenti == 0) {
            nuovaMediaStorica = mediaTempiPartitaCorrente;
        } else {
            nuovaMediaStorica = ((stat.getMediaRisposta() * totalePartitePrecedenti) + mediaTempiPartitaCorrente) / (totalePartitePrecedenti + 1);
        }
        stat.setMediaRisposta(nuovaMediaStorica);

        // Aggiornamento di vittorie e sconfitte
        if (vincitorePartita != null) {
            if (vincitorePartita.getUsername().equals(giocatore.getUsername())) {
                stat.setVittorie(stat.getVittorie() + 1);
            } else {
                stat.setSconfitte(stat.getSconfitte() + 1);
            }
        } else {
            // In caso di pareggio perfetto (nessun vincitore), incrementiamo le sconfitte di entrambi 
            stat.setSconfitte(stat.getSconfitte() + 1);
        }

        // Calcolo della nuova percentuale di vittorie 
        int totalePartiteNuovo = stat.getVittorie() + stat.getSconfitte();
        int nuovaPercentuale = (totalePartiteNuovo > 0) ? (stat.getVittorie() * 100) / totalePartiteNuovo : 0;
        stat.setPercentualeVittorie(nuovaPercentuale);

        // Aggiornamento finale del database
        statisticaDAO.aggiorna(stat);
    }
    
    /**
     * Recupera le statistiche globali di un utente specifico partendo dal suo identificativo.
     * @param username Lo username del giocatore da cercare.
     * @return L'oggetto {@link Statistica} popolato con le metriche storiche, o null se l'utente non ha registrazioni.
     * @throws SQLException In caso di errori di lettura dal database.
     */
    public Statistica getStatistica(String username) throws SQLException {
        return statisticaDAO.cerca(username);
    }
    /**
     * Aggiorna forzatamente sul database i dati contenuti in un'istanza statistica.
     * @param s L'oggetto {@link Statistica} modificato da sovrascrivere sul DB.
     * @throws SQLException Se l'operazione di update fallisce.
     */
    public void aggiornaStatistica(Statistica s) throws SQLException {
        statisticaDAO.aggiorna(s);
    }
    
    /**
     * Registra  un record di statistiche all'interno del database.
     * @param s Il nuovo oggetto {@link Statistica} da inserire a sistema.
     * @throws SQLException In caso di collisione di chiavi o errori di rete con il DB.
     */
    public void creaStatistica(Statistica s) throws SQLException {
        statisticaDAO.aggiungi(s);
    }
}
