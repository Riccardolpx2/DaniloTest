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
 * Gestisce la persistenza e il recupero delle domande di gioco.
 * Mappa gli oggetti {@link Domanda} sulla tabella domande. 
 * * @author Utente
 */
public class DomandaDAO implements DAO<Domanda,Integer>{
 
    /**
     * Inserisce una nuova domanda nel database.
     * Recupera automaticamente l'ID generato (autoincrement)
     * e lo imposta nell'oggetto passato come parametro.
     * Converte le liste paroleSoluzioni e paroleSoluzioniCifrate
     * in stringhe piane separate da virgola prima del salvataggio e sincronizza l'ID generato.
     * @param d L'oggetto Domanda completo da persistere.
     * @throws SQLException Se si verifica un errore di scrittura o se non viene generata la chiave primaria.
     */
@Override
    public void aggiungi(Domanda d) throws SQLException {
        String sql = "INSERT INTO domande (idDocumento, difficolta, testoCifrato, paroleSoluzioni, paroleCifrate) VALUES (?, ?, ?, ?, ?);";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, d.getIdDocumento()); 
            pstmt.setString(2, d.getDifficolta().toUpperCase());
            pstmt.setString(3, d.getTestoCifrato());
            
            String soluzioniFlat = String.join(",", d.getParoleSoluzioni()); 
            String cifrateFlat = String.join(",", d.getParoleSoluzioniCifrate());
            
            pstmt.setString(4, soluzioniFlat);
            pstmt.setString(5, cifrateFlat);
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    d.setIdDomanda(generatedKeys.getInt(1)); 
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
    
    /**
     * Operazione non supportata. La ricerca di una singola domanda per ID non è prevista dal flusso di gioco.
     * @param key L'ID della domanda.
     * @throws UnsupportedOperationException Sempre. Consiglia l'uso di estraiDomandeCasuali.
     */
    @Override
    public Domanda cerca(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Ricerca singola non supportata. Usa estraiDomandeCasuali.");
    }
    
    /**
     * Operazione non supportata. La cancellazione delle singole domande non è prevista; 
     * la rimozione è gestita in cascata dal DBMS (ON DELETE CASCADE) alla rimozione del documento associato.
     * @param d La domanda da rimuovere.
     * @throws UnsupportedOperationException Sempre.
     */
    @Override
    public void rimuovi(Domanda d) throws SQLException {
        // Non serve: la cancellazione è gestita dal DB in cascata (ON DELETE CASCADE) quando elimini un Documento
        throw new UnsupportedOperationException("Rimozione singola domanda non supportata.");
    }

    /**
     * Operazione non supportata. Le domande inserite nel sistema sono considerate immutabili.
     * @param d L'oggetto da aggiornare.
     * @throws UnsupportedOperationException Sempre.
     */
    @Override
    public void aggiorna(Domanda d) throws SQLException {
        throw new UnsupportedOperationException("L'aggiornamento delle domande non è supportato.");
    }
    
    /**
     * Operazione non supportata. Il recupero massivo indiscriminato di tutte le domande non è consentito per motivi di performance.
     * @return Non restituisce alcun valore.
     * @throws UnsupportedOperationException Sempre.
     */
    @Override
    public List<Domanda> elencaTutti() throws SQLException {
        throw new UnsupportedOperationException("Usa il metodo estraiDomandeCasuali filtrato per match.");
    }
    
    /**
     * Estrae un set di domande casuali dal database, filtrate per documento di origine e livello di difficoltà.
     * Sfrutta l'algoritmo nativo ORDER BY RANDOM() limitando il set di risultati 
     * tramite la clausola LIMIT per ottimizzare l'occupazione di memoria sul server.
     * @param idDocumento L'identificativo del documento da cui attingere le domande.
     * @param difficolta Il livello di difficoltà richiesto (es. "FACILE", "MEDIA", "DIFFICILE").
     * @param quantita Il numero massimo di domande da inserire nel round di gioco.
     * @return Una List di oggetti {@link Domanda} pronti per la partita; restituisce una lista vuota se nessun match corrisponde ai filtri.
     * @throws SQLException In caso di problemi di comunicazione con il database o nel parsing dei campi CSV.
     */
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
