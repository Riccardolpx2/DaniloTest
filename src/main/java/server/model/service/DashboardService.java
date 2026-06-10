package server.model.service;

import server.model.database.DatabaseManager;
import server.model.database.StatisticaDAO;
import server.model.database.UtenteDAO;
import shared.game.Statistica;
import shared.protocol.DTO.StatDTO;

public class DashboardService {

    public DashboardService() {

    }


    public StatDTO getStatistiche(String username) throws Exception {
        StatisticaDAO sd = new StatisticaDAO();
        Statistica s =sd.cerca(username);
        if(s==null){
            return null;
        }
        return new StatDTO(s.getPlayer().getUsername(), s.getVittorie(), s.getSconfitte(), s.getPercentualeVittorie(), s.getMediaRisposta());
    }



}
