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
import java.util.Map;
import server.gameUtil.AnalisiTesto;

/**
 *
 * @author Utente
 */
public class AnalisiTestoDAO implements DAO<AnalisiTesto,String>{
    
@Override
public void aggiungi(AnalisiTesto analisi) throws SQLException {
    String sqlParole = "INSERT INTO analisi_parole (idDocumento, parola, frequenza) VALUES (?, ?, ?);";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmtP = conn.prepareStatement(sqlParole)) {
        
        Map<String, Integer> mappaFrequenze = analisi.getFrequenzaParole();
        
        // Prepariamo la lista di tutte le parole
        for (Map.Entry<String, Integer> entry : mappaFrequenze.entrySet()) {
            pstmtP.setInt(1, analisi.getIdDocumento());
            pstmtP.setString(2, entry.getKey());
            pstmtP.setInt(3, entry.getValue());
            pstmtP.addBatch(); // Accumula la parola nel buffer in memoria
        }
        
        // Invia tutto il blocco al database in un'unica operazione atomica
        pstmtP.executeBatch(); 
        
        System.out.println("Analisi parole per il documento ID: " + analisi.getIdDocumento() + " salvata con successo (senza auto-commit manuale).");
    }
}
    
    @Override 
    public void rimuovi(AnalisiTesto analisi) throws SQLException {
        throw new UnsupportedOperationException("Operazione di rimozione non supportata. La cancellazione del documento elimina l'analisi in cascata.");
    }
    
    @Override 
    public void aggiorna(AnalisiTesto analisi) throws SQLException {
        throw new UnsupportedOperationException("Operazione di aggiornamento non supportata");
    }
    
    @Override 
    public AnalisiTesto cerca(String key) throws SQLException {
        String sql = "SELECT parola, frequenza FROM analisi_parole WHERE idDocumento = ?;";
        int idDoc = Integer.parseInt(key);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDoc);

            try (ResultSet rs = pstmt.executeQuery()) {
                // 1. Creiamo l'oggetto usando il tuo costruttore originale
                AnalisiTesto analisi = null;

                // Ricostruiamo la mappa inserendo i dati direttamente nell'oggetto
                while (rs.next()) {
                    if (analisi == null) {
                        analisi = new AnalisiTesto(idDoc); // Istanza creata solo se troviamo dati
                    }
                    String parola = rs.getString("parola");
                    int freq = rs.getInt("frequenza");

                    // Usiamo il nuovo metodo d'appoggio appena creato
                    analisi.aggiungiParolaFrequenza(parola, freq);
                }

                return analisi; // Sarà null se il documento non aveva analisi nel DB
            }
        } catch (SQLException e) {
            System.err.println("Errore nella ricerca dell'analisi: " + e.getMessage());
            throw e;
        }
}
   
    @Override
    public List<AnalisiTesto> elencaTutti() throws SQLException {
        // Estraiamo tutti gli ID documento che possiedono un'analisi
        String sqlId = "SELECT DISTINCT idDocumento FROM analisi_parole;";
        List<AnalisiTesto> listaAnalisi = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlId);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idDoc = rs.getInt("idDocumento");
                // Sfruttiamo il metodo cerca() per caricare l'oggetto completo di mappa per ogni id
                AnalisiTesto analisi = cerca(String.valueOf(idDoc));
                if (analisi != null) {
                    listaAnalisi.add(analisi);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore nell'elencare le analisi: " + e.getMessage());
            throw e;
        }
        return listaAnalisi;
    }
    
}
