/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database;

import java.sql.SQLException;
import java.util.List;
import shared.model.game.Statistica;

/**
 *
 * @author Utente
 */
public class StatisticaDAO implements DAO<Statistica,String>{

    @Override
    public void aggiungi(Statistica st) throws SQLException{
    
    }

    @Override
    public void rimuovi(Statistica st) throws SQLException{
    
    }

    @Override
    public void aggiorna(Statistica st) throws SQLException{
    
    }

    @Override
    public Statistica cerca(String key) throws SQLException{
    
        return null;
    }
   
    @Override
    public List<Statistica> elencaTutti() throws SQLException{
    
        return null;
    }
    
}
