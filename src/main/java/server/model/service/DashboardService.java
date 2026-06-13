package server.model.service;

import server.model.database.StatisticaDAO;
import server.gameUtil.Statistica;
import shared.protocol.DTO.StatDTO;

/**
 * Servizio dedicato alle operazioni disponibili all'interno della dashboard del client.
 * Si occupa di recuperare le informazioni relative allo storico e alle statistiche
 * dell'utente autenticato, convertendole in formati pronti per il trasferimento di rete (DTO).
 */
public class DashboardService {

    /**
     * Costruttore di default per il servizio di dashboard.
     */
    public DashboardService() {

    }


    /**
     * Recupera le statistiche di gioco (vittorie, sconfitte, percentuale e tempi medi) di un giocatore.
     *
     * @param username L'username dell'utente per cui si vogliono recuperare le statistiche.
     * @return Un oggetto {@link StatDTO} popolato con i dati statistici, oppure {@code null} se non ne esistono.
     * @throws Exception In caso di problemi durante l'interrogazione al database.
     */
    public StatDTO getStatistiche(String username) throws Exception {
        StatisticaDAO sd = new StatisticaDAO();
        Statistica s =sd.cerca(username);
        if(s==null){
            return null;
        }
        return new StatDTO(s.getPlayer().getUsername(), s.getVittorie(), s.getSconfitte(), s.getPercentualeVittorie(), s.getMediaRisposta());
    }



}
