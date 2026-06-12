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
 *
 * @author Utente
 */
public class GameService {

private final PartitaDAO partitaDAO = new PartitaDAO();
private final StatisticaDAO statisticaDAO = new StatisticaDAO();

    /**
     * Salva la partita (compresi i tempi dei round in batch) e aggiorna 
     * le statistiche globali di entrambi i giocatori.
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
     * Metodo di supporto privato per ricalcolare e salvare le statistiche del singolo giocatore.
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

        // 1. CALCOLO DELLA MEDIA DEI TEMPI DI QUESTA PARTITA
        double mediaTempiPartitaCorrente = tempiPartita.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(30.0); // Default a 30 secondi in caso di liste vuote (timeout)

        // 2. CALCOLO DELLA NUOVA MEDIA PONDERATA STORICA
        int totalePartitePrecedenti = stat.getVittorie() + stat.getSconfitte();
        double nuovaMediaStorica;
        
        if (totalePartitePrecedenti == 0) {
            nuovaMediaStorica = mediaTempiPartitaCorrente;
        } else {
            nuovaMediaStorica = ((stat.getMediaRisposta() * totalePartitePrecedenti) + mediaTempiPartitaCorrente) / (totalePartitePrecedenti + 1);
        }
        stat.setMediaRisposta(nuovaMediaStorica);

        // 3. AGGIORNAMENTO DI VITTORIE E SCONFITTE
        if (vincitorePartita != null) {
            if (vincitorePartita.getUsername().equals(giocatore.getUsername())) {
                stat.setVittorie(stat.getVittorie() + 1);
            } else {
                stat.setSconfitte(stat.getSconfitte() + 1);
            }
        } else {
            // In caso di pareggio perfetto (nessun vincitore), incrementiamo le sconfitte di entrambi 
            // o lasciamo invariato a seconda delle vostre regole di business. Qui incrementiamo le sconfitte.
            stat.setSconfitte(stat.getSconfitte() + 1);
        }

        // 4. CALCOLO DELLA NUOVA PERCENTUALE DI VITTORIE
        int totalePartiteNuovo = stat.getVittorie() + stat.getSconfitte();
        int nuovaPercentuale = (totalePartiteNuovo > 0) ? (stat.getVittorie() * 100) / totalePartiteNuovo : 0;
        stat.setPercentualeVittorie(nuovaPercentuale);

        // 5. UPDATE FINALE SUL DATABASE
        statisticaDAO.aggiorna(stat);
    }

    public Statistica getStatistica(String username) throws SQLException {
        return statisticaDAO.cerca(username);
    }

    public void aggiornaStatistica(Statistica s) throws SQLException {
        statisticaDAO.aggiorna(s);
    }

    public void creaStatistica(Statistica s) throws SQLException {
        statisticaDAO.aggiungi(s);
    }
}
