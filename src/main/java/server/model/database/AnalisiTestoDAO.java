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
import server.gameLogic.AnalisiTesto;

/**
 * Gestione della persistenza dei report di analisi testuale.
 * Implementa l'interfaccia generica {@link DAO} mappando gli oggetti {@link AnalisiTesto}
 * sulla tabella analisi_parole. Gestisce il salvataggio di mappe di frequenza
 * (coppie parola-conteggio) collegate in modo referenziale a un singolo documento di gioco.
 * @author Utente
 */
public class AnalisiTestoDAO implements DAO<AnalisiTesto,Integer>{

    /**
     * Inserisce i dati di analisi di un testo all'interno del database.
     * Estrae la mappa delle frequenze dall'oggetto di dominio e memorizza ogni singola voce 
     * sfruttando l'ottimizzazione in modalità batch per ridurre il carico sul DBMS.
     * @param analisi L'oggetto AnalisiTesto contenente l'ID del documento e la mappa delle frequenze delle parole.
     * @throws SQLException Se si verificano errori nell'esecuzione del batch o nella comunicazione con il DB.
     */
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
            // Accumula la parola nel buffer in memoria
            pstmtP.addBatch(); 
        }
        
        // Invia tutto il blocco al database in un'unica operazione atomica
        pstmtP.executeBatch(); 
        
        System.out.println("Analisi parole per il documento ID: " + analisi.getIdDocumento() + " salvata con successo (senza auto-commit manuale).");
    }
}
    
    /**
     * Operazione non supportata. La rimozione delle analisi avviene in cascata lato DBMS 
     * (ON DELETE CASCADE) a seguito della cancellazione del record del documento principale.
     * @param analisi L'entità di analisi di cui si richiede la cancellazione.
     * @throws UnsupportedOperationException Sempre, delegando il vincolo referenziale al database.
     */
    @Override 
    public void rimuovi(AnalisiTesto analisi) throws SQLException {
        throw new UnsupportedOperationException("Operazione di rimozione non supportata. La cancellazione del documento elimina l'analisi in cascata.");
    }
    /**
     * Operazione non supportata. I dati di analisi calcolati su testi immutabili sono considerati anch'essi immutabili.
     * @param analisi L'entità da aggiornare.
     * @throws UnsupportedOperationException Sempre.
     */
    @Override 
    public void aggiorna(AnalisiTesto analisi) throws SQLException {
        throw new UnsupportedOperationException("Operazione di aggiornamento non supportata");
    }
    
    /**
     * Ricerca e ricostruisce la mappa di analisi statistica associata a uno specifico documento.
     * @param key L'ID numerico del documento.
     * @return Un'istanza completa di {@link AnalisiTesto}, oppure null se l'analisi non esiste.
     * @throws SQLException In caso di anomalie sul database.
     */
    @Override 
    public AnalisiTesto cerca(Integer key) throws SQLException {
        String sql = "SELECT parola, frequenza FROM analisi_parole WHERE idDocumento = ?;";
  

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, key);

            try (ResultSet rs = pstmt.executeQuery()) {
  
                AnalisiTesto analisi = null;

                while (rs.next()) {
                    if (analisi == null) {
                        analisi = new AnalisiTesto(key); 
                    }
                    String parola = rs.getString("parola");
                    int freq = rs.getInt("frequenza");
                    analisi.aggiungiParolaFrequenza(parola, freq);
                }

                return analisi; 
            }
        } catch (SQLException e) {
            System.err.println("Errore nella ricerca dell'analisi: " + e.getMessage());
            throw e;
        }
}
   
     /**
     * Estrae l'elenco globale di tutti i report di analisi testuale presenti a sistema.
     * Il metodo esegue una prima query per isolare gli identificativi univoci dei documenti 
     * che possiedono un'analisi. Successivamente, per ciascun ID trovato, istanzia l'oggetto 
     * di dominio e ne delega il completo popolamento della mappa interna al metodo privato 
     * {@link #recuperaMappaParole(Connection, AnalisiTesto)}, riutilizzando la medesima 
     * connessione fisica al database.
     * @return Una {@link List} contenente tutti gli oggetti {@link AnalisiTesto} censiti nel database. 
     * Se la tabella è vuota, restituisce una lista vuota.
     * @throws SQLException Se si verificano anomalie o errori di comunicazione durante la lettura massiva.
     */
    @Override
    public List<AnalisiTesto> elencaTutti() throws SQLException {
        String sqlId = "SELECT DISTINCT idDocumento FROM analisi_parole;";
        List<AnalisiTesto> listaAnalisi = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlId);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idDoc = rs.getInt("idDocumento");
            AnalisiTesto analisi = new AnalisiTesto(idDoc);
            
            recuperaMappaParole(conn, analisi);
            
            listaAnalisi.add(analisi);
            }
        } catch (SQLException e) {
            System.out.println("Errore nell'elencare le analisi: " + e.getMessage());
            throw e;
        }
        return listaAnalisi;
    }
    
    /**
     * Metodo di supporto privato che ripopola la mappa delle frequenze delle parole 
     * per una specifica istanza di analisi.
     * Interroga la tabella analisi_parole filtrando per l'ID del documento 
     * associato e inserisce progressivamente le coppie (parola, frequenza) estratte 
     * dal {@link ResultSet} direttamente nell'oggetto passato come parametro.
     * @param conn    La connessione SQL attiva ereditata dal metodo chiamante.
     * @param analisi L'istanza di {@link AnalisiTesto} da completare con i dati di dettaglio.
     * @throws SQLException Se si verificano errori nell'esecuzione della query di selezione delle parole.
     */
    private void recuperaMappaParole(Connection conn, AnalisiTesto analisi) throws SQLException {
    String sql = "SELECT parola, frequenza FROM analisi_parole WHERE idDocumento = ?;";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, analisi.getIdDocumento());
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                analisi.aggiungiParolaFrequenza(rs.getString("parola"), rs.getInt("frequenza"));
            }
        }
    }
  }  
}
