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
import shared.game.Documento;

/**
 *
 * @author Utente
 */
public class DocumentoDAO implements DAO<Documento,Integer>{
    
    @Override
    public void aggiungi(Documento d) throws SQLException{
    String sql = "INSERT INTO documenti (nome, testo) VALUES (?, ?);";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        
        pstmt.setString(1, d.getNome());
        pstmt.setString(2, d.getTesto());
        
        pstmt.executeUpdate();

        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                d.setId(generatedKeys.getInt(1));
            }
        }
        System.out.println("Documento '" + d.getNome() + "' inserito con successo con ID: " + d.getId());
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    }
    

    @Override
    public void rimuovi(Documento d) throws SQLException{
    String sql = "DELETE FROM documenti WHERE id = ?;";
    
    try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
    pstmt.setInt(1, d.getId());
    
    int righeEliminate = pstmt.executeUpdate();
    
    if(righeEliminate > 0) System.out.println("Documento (id: " + d.getId() + " ) e tutte le sue parole-frequenze eliminate a cascata con successo!");
    else System.out.println("Nessun documento trovato con ID: " + d.getId());
        
            }catch(SQLException e){
            System.out.println(e.getMessage());
            }
    }

    @Override
    public void aggiorna(Documento d) throws SQLException{
    throw new UnsupportedOperationException("L'aggiornamento dei documenti non è supportato in questo gioco.");
    }

    @Override
    public Documento cerca(Integer key) throws SQLException{
    String sqlDoc = "SELECT id, nome, testo FROM documenti WHERE id = ?;";
    Documento doc = null;
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmtDoc = conn.prepareStatement(sqlDoc)) {      
        pstmtDoc.setInt(1, key); 
        
        try (ResultSet rsDoc = pstmtDoc.executeQuery()) {
            if (rsDoc.next()) {
                int id = rsDoc.getInt("id");
                String nome = rsDoc.getString("nome");
                String testo = rsDoc.getString("testo");

                doc = new Documento(id, nome, testo);
            }
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    return doc;
    }
   
    @Override
    public List<Documento> elencaTutti() throws SQLException{
    String sql = "SELECT id, nome, testo FROM documenti;";
    List<Documento> listaDocumenti = new ArrayList<>();

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String nome = rs.getString("nome");
            String testo = rs.getString("testo");

            Documento doc = new Documento(id, nome, testo);
            listaDocumenti.add(doc);
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    return listaDocumenti;
    }    
    
}
