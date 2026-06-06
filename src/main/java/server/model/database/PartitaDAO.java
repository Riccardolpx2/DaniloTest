/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database;

import java.sql.SQLException;
import java.util.List;
import shared.game.Partita;

/**
 *
 * @author Utente
 */
public class PartitaDAO implements DAO<Partita,Integer>{
    
    
    @Override
    public void aggiungi(Partita p) throws SQLException{
    
    }

    @Override
    public void rimuovi(Partita p) throws SQLException{
    
    }

    @Override
    public void aggiorna(Partita p) throws SQLException{
    
    }

    @Override
    public Partita cerca(Integer key) throws SQLException{
    
        return null;
    }
   
    @Override
    public List<Partita> elencaTutti() throws SQLException{
    
        return null;
    } 
    
    
}
