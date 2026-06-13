/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import server.gameUtil.Domanda;

/**
 *
 * @author Utente
 */
public class DomandaDAO implements DAO<Domanda,Integer>{
    
@Override
    public void aggiungi(Domanda d) throws SQLException {
        String sql = "INSERT INTO domande (idDocumento, difficolta, testoCifrato, paroleSoluzioni, paroleCifrate) VALUES (?, ?, ?, ?, ?);";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, d.getIdDocumento()); 
            pstmt.setString(2, d.getDifficolta().toUpperCase());
            pstmt.setString(3, d.getTestoCifrato());
            
            // Mappatura esatta sui tuoi getter: paroleSoluzioni e paroleSoluzioniCifrate
            String soluzioniFlat = String.join(",", d.getParoleSoluzioni()); 
            String cifrateFlat = String.join(",", d.getParoleSoluzioniCifrate());
            
            pstmt.setString(4, soluzioniFlat);
            pstmt.setString(5, cifrateFlat);
            
            pstmt.executeUpdate();
            
            // Recuperiamo la chiave primaria autogenerata da SQLite
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    d.setIdDomanda(generatedKeys.getInt(1)); // Sincronizziamo l'oggetto Java
                } else {
                    throw new SQLException("Errore: Inserimento fallito, nessun idDomanda generato.");
                }
            }
            System.out.println("Domanda salvata con successo. ID Assegnato dal DB: " + d.getIdDomanda());
        } catch (SQLException e) {
            System.out.println("Errore nel metodo aggiungi di DomandaDAO: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Domanda cerca(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Ricerca singola non supportata. Usa estraiDomandeCasuali.");
    }

    @Override
    public void rimuovi(Domanda d) throws SQLException {
        // Non serve: la cancellazione è gestita dal DB in cascata (ON DELETE CASCADE) quando elimini un Documento
        throw new UnsupportedOperationException("Rimozione singola domanda non supportata.");
    }

    @Override
    public void aggiorna(Domanda d) throws SQLException {
        throw new UnsupportedOperationException("L'aggiornamento delle domande non è supportato.");
    }

    @Override
    public List<Domanda> elencaTutti() throws SQLException {
        throw new UnsupportedOperationException("Usa il metodo estraiDomandeCasuali filtrato per match.");
    }
    
    public List<Domanda> estraiDomandeCasuali(int idDocumento, String difficolta, int quantita) throws SQLException {
        List<Domanda> domande = new ArrayList<>();

        String sql = "SELECT idDomanda, testoCifrato, paroleSoluzioni, paroleCifrate FROM domande " +
                     "WHERE idDocumento = ? AND difficolta = ? ORDER BY RANDOM() LIMIT ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDocumento);
            pstmt.setString(2, difficolta.toUpperCase());
            pstmt.setInt(3, quantita);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idDomanda = rs.getInt("idDomanda");
                    String testoCifrato = rs.getString("testoCifrato");

                    List<String> soluzioni = Arrays.asList(rs.getString("paroleSoluzioni").split(","));
                    List<String> cifrate = Arrays.asList(rs.getString("paroleCifrate").split(","));

                    Domanda d = new Domanda(idDomanda, idDocumento, testoCifrato, soluzioni, cifrate, difficolta);

                    domande.add(d); // Appena la lista raggiunge la dimensione di 'quantita', il ciclo si interrompe
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore nell'estrazione casuale delle domande: " + e.getMessage());
            throw e;
        }
        return domande;
    }
}
