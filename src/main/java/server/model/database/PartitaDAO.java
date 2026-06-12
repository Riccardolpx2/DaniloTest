/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import server.model.database.entity.UtenteEntity;
import server.gameUtil.Documento;
import server.gameUtil.Partita;

/**
 *
 * @author Utente
 */
public class PartitaDAO implements DAO<Partita,Integer>{
    
    
    @Override
    public void aggiungi(Partita p) throws SQLException{
    String sql = "INSERT INTO partite (idSessione, offsetIniziale, lunghezza, shiftCesare, parolaSoluzione, secondiRispostaG1, "
            + "secondiRispostaG2, difficolta, vincitore, idDocumento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, p.getIdSessione());
            pstmt.setInt(2, p.getOffsetIniziale());
            pstmt.setInt(3, p.getLunghezza());
            pstmt.setInt(4, p.getShiftCesare());
            pstmt.setString(5, p.getParolaSoluzione());
            pstmt.setInt(6, p.getSecondiRispostaG1());
            pstmt.setInt(7, p.getSecondiRispostaG2());
            pstmt.setString(8, p.getDifficolta());
            
            if (p.getVincitore() != null) pstmt.setString(9, p.getVincitore().getUsername()); 
            else pstmt.setNull(9, java.sql.Types.VARCHAR);
            
            if (p.getDocumento() != null) pstmt.setInt(10, p.getDocumento().getIdDocumento());
            else pstmt.setNull(10, java.sql.Types.INTEGER);
            
            
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                p.setIdPartita(generatedKeys.getInt(1));
            }
        }
        System.out.println("Partita inserita con successo con ID: " + p.getIdPartita());
        } catch (SQLException e) {
        System.out.println(e.getMessage());
        throw e;
        }       
        
    
    }

    @Override
    public void rimuovi(Partita p) throws SQLException{
    throw new UnsupportedOperationException("Operazione di rimozione non supportata per la cronologia delle partite.");
    }

    @Override
    public void aggiorna(Partita p) throws SQLException{
    throw new UnsupportedOperationException("Operazione di aggiornamento non supportata. Le partite sono immutabili.");
    }

    @Override
    public Partita cerca(Integer key) throws SQLException{
    String sqlPar= "SELECT * FROM partite WHERE idPartita = ?;";
    Partita p = null;
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmtDoc = conn.prepareStatement(sqlPar)) {      
        pstmtDoc.setInt(1, key); 
        
        try (ResultSet rsP = pstmtDoc.executeQuery()) {
            if (rsP.next()) {
                int id = rsP.getInt("idPartita");
                int idSessione = rsP.getInt("idSessione");
                int offset = rsP.getInt("offsetIniziale");
                int lunghezza = rsP.getInt("lunghezza");
                int shift = rsP.getInt("shiftCesare");
                String soluzione = rsP.getString("parolaSoluzione");
                int secondiG1 = rsP.getInt("secondiRispostaG1");
                int secondiG2 = rsP.getInt("secondiRispostaG2");
                String difficolta = rsP.getString("difficolta");
                
                UtenteEntity vincitore = null;
                String usernameVincitore = rsP.getString("vincitore");
                if (usernameVincitore != null) vincitore = new UtenteEntity(usernameVincitore, null, null, null, null);
                
                Documento documento = null;
                int idDoc = rsP.getInt("idDocumento");
                if (idDoc > 0) documento = new Documento(idDoc, null, null);                  
                p = new Partita(id, idSessione,offset,lunghezza,shift,soluzione, secondiG1,secondiG2,difficolta,vincitore,documento);
                        
            }
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
        throw e;
    }
    return p;
    }
   
    @Override
    public List<Partita> elencaTutti() throws SQLException{
    String sql = "SELECT * FROM partite;";
    List<Partita> listaPartite = new ArrayList<>();

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rsP = pstmt.executeQuery()) {

        while (rsP.next()) {
            int id = rsP.getInt("idPartita");
            int idSessione = rsP.getInt("idSessione");
            int offset = rsP.getInt("offsetIniziale");
            int lunghezza = rsP.getInt("lunghezza");
            int shift = rsP.getInt("shiftCesare");
            String soluzione = rsP.getString("parolaSoluzione");
            int secondiG1 = rsP.getInt("secondiRispostaG1");
            int secondiG2 = rsP.getInt("secondiRispostaG2");
            String difficolta = rsP.getString("difficolta");
            
            UtenteEntity vincitore = null;
            String usernameVincitore = rsP.getString("vincitore");
            if (usernameVincitore != null) vincitore = new UtenteEntity(usernameVincitore, null, null, null, null);
                
            Documento documento = null;
            int idDoc = rsP.getInt("idDocumento");
            if (idDoc > 0) documento = new Documento(idDoc, null, null);   
            
            Partita p = new Partita(id,idSessione,offset,lunghezza,shift,soluzione,secondiG1,secondiG2,difficolta,vincitore,documento);
                 
            listaPartite.add(p);
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
        throw e;
    }
    return listaPartite;
    }
    
}
