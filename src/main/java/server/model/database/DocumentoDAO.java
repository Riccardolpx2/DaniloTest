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
import server.gameUtil.Documento;

/**
 * Gestisce la persistenza dei dati della tabella documenti.
 * Implementa l'interfaccia generica {@link DAO} mappando gli oggetti {@link Documento}
 * usando l'identificativo numerico autoincrementante (Integer) come chiave primaria.
 * @author Utente
 */
public class DocumentoDAO implements DAO<Documento,Integer>{
    
    /**
     * Inserisce un nuovo documento nel database.
     * Recupera automaticamente l'ID generato (autoincrement)
     * e lo imposta nell'oggetto passato come parametro.
     * @param d L'oggetto Documento contenente nome e testo da salvare.
     * @throws SQLException Se si verificano errori di connessione o di scrittura sul database.
     */
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
                d.setIdDocumento(generatedKeys.getInt(1));
            }
        }
        System.out.println("Documento '" + d.getNome() + "' inserito con successo con ID: " + d.getIdDocumento());
    } catch (SQLException e) {
        System.out.println(e.getMessage());
        throw e;
        
    }
    }
    
    /**
     * Rimuove un documento dal database basandosi sul suo ID univoco.
     * @param d L'oggetto Documento da eliminare (deve contenere un idDocumento valido).
     * @throws SQLException Se fallisce l'operazione di cancellazione o per violazione di chiavi esterne.
     */
    @Override
    public void rimuovi(Documento d) throws SQLException{
    String sql = "DELETE FROM documenti WHERE idDocumento = ?;";
    
    try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
    pstmt.setInt(1, d.getIdDocumento());
    
    int righeEliminate = pstmt.executeUpdate();
    
    if(righeEliminate > 0) System.out.println("Documento (id: " + d.getIdDocumento() + " ) eliminato con successo!");
    else System.out.println("Nessun documento trovato con ID: " + d.getIdDocumento());
        
            }catch(SQLException e){
            System.out.println(e.getMessage());
            throw e;
            }
    }

    /**
     * Operazione non supportata. L'aggiornamento dei documenti non è previsto dalle regole di dominio.
     * @param d L'entità che si vorrebbe aggiornare.
     * @throws UnsupportedOperationException Sempre, poiché la modifica dei testi non è consentita.
     * @throws SQLException Non viene lanciata in questo contesto ma è presente per firma dell'interfaccia.
     */
    @Override
    public void aggiorna(Documento d) throws SQLException{
    throw new UnsupportedOperationException("L'aggiornamento dei documenti non è supportato in questo gioco.");
    }
    
    /**
     * Cerca un documento specifico nel database tramite il suo ID numerico.
     * @param key L'idDocumento del record da cercare.
     * @return L'oggetto Documento mappato se trovato, null altrimenti.
     * @throws SQLException Se si verificano errori nell'esecuzione della query di selezione.
     */
    @Override
    public Documento cerca(Integer key) throws SQLException{
    String sqlDoc = "SELECT idDocumento, nome, testo FROM documenti WHERE idDocumento = ?;";
    Documento doc = null;
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmtDoc = conn.prepareStatement(sqlDoc)) {      
        pstmtDoc.setInt(1, key); 
        
        try (ResultSet rsDoc = pstmtDoc.executeQuery()) {
            if (rsDoc.next()) {
                int id = rsDoc.getInt("idDocumento");
                String nome = rsDoc.getString("nome");
                String testo = rsDoc.getString("testo");

                doc = new Documento(id, nome, testo);
            }
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
        throw e;
    }
    return doc;
    }
    
    /**
     * Recupera l'elenco di tutti i documenti memorizzati nella tabella.
     * @return Una List di oggetti Documento. Se la tabella è vuota restituisce una lista vuota.
     * @throws SQLException In caso di errori di lettura dal database.
     */
    @Override
    public List<Documento> elencaTutti() throws SQLException{
    String sql = "SELECT idDocumento, nome, testo FROM documenti;";
    List<Documento> listaDocumenti = new ArrayList<>();

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("idDocumento");
            String nome = rs.getString("nome");
            String testo = rs.getString("testo");

            Documento doc = new Documento(id, nome, testo);
            listaDocumenti.add(doc);
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
        throw e;
    }
    return listaDocumenti;
    }  
    
    
    /**
     * Seleziona ed estrae un singolo documento in modo completamente casuale dalla tabella.
     * Sfrutta l'algoritmo di ordinamento del DBMS tramite la clausola ORDER BY RANDOM().
     * @return Un'istanza di Documento scelta casualmente, oppure null se la tabella è vuota.
     * @throws SQLException In caso di problemi di comunicazione con il database.
     */
    public Documento estraiDocumentoCasuale() throws SQLException {
    // Ordina i documenti in modo casuale 
    String sql = "SELECT * FROM documenti ORDER BY RANDOM() LIMIT 1;";
    
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        // Spostiamo il cursore sul primo record rimescolato verrà letta solo questa riga dal flusso di rete, ignorando il resto.
        if (rs.next()) {
            int id = rs.getInt("idDocumento");
            String nome = rs.getString("nome");
            String testo = rs.getString("testo");
            return new Documento(id, nome, testo);
        }
    }
    return null; 
    }
    
}
