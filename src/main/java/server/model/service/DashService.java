package server.model.service;

import server.model.database.UtenteDAO;
import shared.game.Statistica;
import shared.protocol.DTO.StatDTO;

public class DashService {

    private UtenteDAO utenteDAO;

    public DashService() {
        this.utenteDAO = new UtenteDAO();
    }

    //todo metodo per trovare stat e avvio partite
    public StatDTO getStatistiche(String username) throws Exception {
        return  null;
    }



}
