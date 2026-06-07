/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import server.model.database.entity.UtenteEntity;
import shared.game.Documento;
import shared.game.Partita;
import shared.game.SessioneDiGioco;

/**
 *
 * @author Utente
 */
public class SessioneDiGiocoDAO implements DAO<SessioneDiGioco,Integer>{
    
    @Override
    public void aggiungi(SessioneDiGioco s) throws SQLException{
        String sql = "INSERT INTO sessioni (utente1, utente2, vincitore, stato, data_ora, durataSessione,punteggioG1,punteggioG2) "
                + "VALUES (?, ?, ?, ?, ?, ?,?,?)";
    
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, s.getPlayer1().getUsername());
            pstmt.setString(2, s.getPlayer2().getUsername());
            
            if (s.getVincitore() != null) pstmt.setString(3, s.getVincitore().getUsername()); 
            else pstmt.setNull(3, java.sql.Types.VARCHAR); 
            
            pstmt.setString(4,s.getStato());
            if (s.getDataInizio() != null) pstmt.setString(5, s.getDataInizio().toString());
            else pstmt.setNull(5, java.sql.Types.VARCHAR);
            pstmt.setInt(6, s.getDurataSessione());
            pstmt.setInt(7, s.getPunteggioTotaleG1()); 
            pstmt.setInt(8, s.getPunteggioTotaleG2());
                                
            
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                s.setIdSessione(generatedKeys.getInt(1));
            }
        }
        System.out.println("Sessione inserita con successo con ID: " + s.getIdSessione());
        }catch (SQLException e) {
        System.out.println(e.getMessage());
        throw e;
        }   
    }

    @Override
    public void rimuovi(SessioneDiGioco s) throws SQLException{
        String sql = "DELETE FROM sessioni WHERE idSessione = ?;";
    
    try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
    pstmt.setInt(1, s.getIdSessione());
    
    int righeEliminate = pstmt.executeUpdate();
    
    if(righeEliminate > 0) System.out.println("Sessione (id: " + s.getIdSessione() + " ) e tutte le sue partite eliminate a cascata con successo!");
    else System.out.println("Nessuna sessione trovato con ID: " + s.getIdSessione());
        
            }catch(SQLException e){
            System.out.println(e.getMessage());
            throw e;
            }
    }

    @Override
    public void aggiorna(SessioneDiGioco s) throws SQLException{
    String sql = "UPDATE sessioni SET vincitore = ?, stato = ?, durataSessione = ?, punteggioG1 = ?, punteggioG2=? WHERE idSessione = ?;";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        if (s.getVincitore() != null) {
            pstmt.setString(1, s.getVincitore().getUsername());
        } else {
            pstmt.setNull(1, java.sql.Types.VARCHAR);
        }
        
        pstmt.setString(2, s.getStato());
        pstmt.setInt(3, s.getDurataSessione());
        pstmt.setInt(4, s.getPunteggioTotaleG1());
        pstmt.setInt(5, s.getPunteggioTotaleG2()); 
        pstmt.setInt(6, s.getIdSessione());
        
        int righeModificate = pstmt.executeUpdate();
        System.out.println("Sessione aggiornata con successo. Righe modificate: " + righeModificate);
        
    } catch (SQLException e) {
        System.out.println(e.getMessage());
        throw e;
    }
    
    }

    @Override
    public SessioneDiGioco cerca(Integer key) throws SQLException{
        String sql = "SELECT * FROM sessioni WHERE idSessione = ?";
        String sqlPartite = "SELECT * FROM partite WHERE idSessione = ?";
        
        SessioneDiGioco s = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement pstmtPart = conn.prepareStatement(sqlPartite)) {
            pstmt.setInt(1, key);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idSessione");
                    int durata = rs.getInt("durataSessione");
                    String stato = rs.getString("stato");
                    String dataStr = rs.getString("data_ora");
                    LocalDateTime dataInizio = (dataStr != null) ? LocalDateTime.parse(dataStr.replace(" ", "T")) : null;
                    
                    int punteggioG1 = rs.getInt("punteggioG1");
                    int punteggioG2 = rs.getInt("punteggioG2");

                    UtenteEntity p1 = new UtenteEntity(rs.getString("utente1"), null, null, null, null);
                    UtenteEntity p2 = new UtenteEntity(rs.getString("utente2"), null, null, null, null);

                    UtenteEntity vincitore = null;
                    String usernameVincitore = rs.getString("vincitore");
                    if (usernameVincitore != null) {
                        vincitore = new UtenteEntity(usernameVincitore, null, null, null, null);
                    }
                    
                    List<Partita> listaPartite = new ArrayList<>();
                    pstmtPart.setInt(1, id);
                    
                    try (ResultSet rsP = pstmtPart.executeQuery()) {
                        while (rsP.next()) {
                            int idPartita = rsP.getInt("idPartita");
                            int offset = rsP.getInt("offsetIniziale");
                            int lunghezza = rsP.getInt("lunghezza");
                            int shift = rsP.getInt("shiftCesare");
                            String soluzione = rsP.getString("parolaSoluzione");
                            int secondiG1 = rsP.getInt("secondiRispostaG1");
                            int secondiG2 = rsP.getInt("secondiRispostaG2");
                            String difficolta = rsP.getString("difficolta");
                            
                            UtenteEntity vincitorePartita = null;
                            String userVincitoreP = rsP.getString("vincitore");
                            if (userVincitoreP != null) {
                                vincitorePartita = new UtenteEntity(userVincitoreP, null, null, null, null);
                            }
                            
                            Documento doc = null;
                            int idDoc = rsP.getInt("idDocumento");
                            if (idDoc > 0) {
                                doc = new Documento(idDoc, null, null);
                            }
                            Partita partita = new Partita(idPartita, id, offset, lunghezza, shift, soluzione, secondiG1, secondiG2, difficolta, vincitorePartita, doc);
                            listaPartite.add(partita);
                        }
                    }
                    s = new SessioneDiGioco(id, durata, listaPartite, dataInizio, punteggioG1, punteggioG2, vincitore, p1, p2, stato);
                }
            }       
        } catch(SQLException e){
            System.out.println(e.getMessage());
            throw e;
        }
        return s;
    }
   
    @Override
    public List<SessioneDiGioco> elencaTutti() throws SQLException{
        String sql = "SELECT * FROM sessioni";
        List<SessioneDiGioco> lista = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idSessione");
                int durata = rs.getInt("durataSessione");
                String stato = rs.getString("stato");
                String dataStr = rs.getString("data_ora");
                LocalDateTime dataInizio = (dataStr != null) ? LocalDateTime.parse(dataStr.replace(" ", "T")) : null;
                int punteggioG1 = rs.getInt("punteggioG1");
                int punteggioG2 = rs.getInt("punteggioG2");
                
                UtenteEntity p1 = new UtenteEntity(rs.getString("utente1"), null, null, null, null);
                UtenteEntity p2 = new UtenteEntity(rs.getString("utente2"), null, null, null, null);
                
                UtenteEntity vincitore = null;
                String usernameVincitore = rs.getString("vincitore");
                if (usernameVincitore != null) {
                    vincitore = new UtenteEntity(usernameVincitore, null, null, null, null);
                }
                SessioneDiGioco s = new SessioneDiGioco(id, durata, null, dataInizio, punteggioG1, punteggioG2, vincitore, p1, p2, stato);
                lista.add(s);
            }
    }catch (SQLException e) {
        System.out.println(e.getMessage());
        throw e;
    }    
  return lista;  
}
}
