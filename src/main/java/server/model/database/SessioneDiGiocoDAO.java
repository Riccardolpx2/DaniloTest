/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database;

import java.sql.SQLException;
import java.util.List;
import shared.game.SessioneDiGioco;

/**
 *
 * @author Utente
 */
public class SessioneDiGiocoDAO implements DAO<SessioneDiGioco,Integer>{
    
    @Override
    public void aggiungi(SessioneDiGioco s) throws SQLException{
    
    }

    @Override
    public void rimuovi(SessioneDiGioco s) throws SQLException{
    
    }

    @Override
    public void aggiorna(SessioneDiGioco s) throws SQLException{
    
    }

    @Override
    public SessioneDiGioco cerca(Integer key) throws SQLException{
    
        return null;
    }
   
    @Override
    public List<SessioneDiGioco> elencaTutti() throws SQLException{
    
        return null;
    }    
    
}
