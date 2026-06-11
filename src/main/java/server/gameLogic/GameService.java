/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameLogic;

import java.sql.SQLException;
import server.model.database.PartitaDAO;
import server.model.database.SessioneDiGiocoDAO;
import server.model.database.StatisticaDAO;
import shared.game.Partita;
import shared.game.SessioneDiGioco;
import shared.game.Statistica;

/**
 *
 * @author Utente
 */
public class GameService {
    
    private final PartitaDAO partitaDAO = new PartitaDAO();
    private final SessioneDiGiocoDAO sessioneDAO = new SessioneDiGiocoDAO();
    private final StatisticaDAO statisticaDAO = new StatisticaDAO();

    public void salvaPartita(Partita p) throws SQLException {
        partitaDAO.aggiungi(p);
    }

    public void salvaSessione(SessioneDiGioco s) throws SQLException {
        sessioneDAO.aggiorna(s);
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
