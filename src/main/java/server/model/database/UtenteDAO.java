package server.model.database;

import server.model.database.entity.UtenteEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestione della persistenza dei dati degli utenti.
 * Implementa l'interfaccia generica {@link DAO} mappando gli oggetti {@link UtenteEntity}
 * sulla tabella utenti, utilizzando lo username del giocatore (String) come chiave primaria.
 */
public class UtenteDAO implements DAO<UtenteEntity,String> {
    
    /**
     * Inserisce un nuovo profilo utente (giocatore) all'interno del database.
     * @param el L'oggetto UtenteEntity contenente le credenziali e i dati anagrafici.
     * @throws SQLException Se lo username è già presente (violazione della PRIMARY KEY) 
     * o se si verificano errori di connessione.
     */
    @Override
    public void aggiungi(UtenteEntity el) throws SQLException {
        String sql = "INSERT INTO utenti(username, password, nome, cognome, data_nascita) VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, el.getUsername());
            pstmt.setString(2, el.getPassword());
            pstmt.setString(3, el.getNome());
            pstmt.setString(4, el.getCognome());
            pstmt.setString(5, el.getDataNascita());
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    
    /**
     * Rimuove un utente dal database partendo dal suo username.
     * Nota: data la configurazione dello schema, l'eliminazione di un utente provocherà 
     * la cancellazione in cascata delle sue statistiche e delle sue partite.
     * @param el L'oggetto UtenteEntity da rimuovere (identificato tramite lo username).
     * @throws SQLException Se si verificano errori durante l'esecuzione della query di eliminazione.
     */
    @Override
    public void rimuovi(UtenteEntity el) throws SQLException {
        String sql = "DELETE FROM utenti WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, el.getUsername());
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    
    /**
     * Aggiorna i dati anagrafici e la password di un utente già esistente nel database.
     * @param el L'oggetto UtenteEntity aggiornato in memoria da persistere.
     * @throws SQLException In caso di problemi di comunicazione con il database.
     */
    @Override
    public void aggiorna(UtenteEntity el) throws SQLException {
        String sql = "UPDATE utenti SET password = ?, nome = ?, cognome = ?, data_nascita = ? WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, el.getPassword());
            pstmt.setString(2, el.getNome());
            pstmt.setString(3, el.getCognome());
            pstmt.setString(4, el.getDataNascita());
            pstmt.setString(5, el.getUsername());
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cerca ed estrae un utente specifico dal database tramite il suo username (chiave primaria).
     * @param key Lo username dell'utente da cercare.
     * @return Un'istanza di {@link UtenteEntity} interamente popolata se trovata, 
     * oppure null se non corrisponde ad alcun record.
     * @throws SQLException Se si verificano anomalie nella lettura del ResultSet o di connessione.
     */
    @Override
    public UtenteEntity cerca(String key) throws SQLException {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, key);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UtenteEntity(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("data_nascita")
                    );
                }
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * Recupera la lista completa di tutti gli utenti registrati all'interno del sistema.
     * @return Una List contenente tutti gli oggetti UtenteEntity trovati. 
     * Se la tabella è vuota, restituisce una lista vuota.
     * @throws SQLException In caso di errori durante l'estrazione massiva dei record.
     */
    @Override
    public List<UtenteEntity> elencaTutti() throws SQLException {
        List<UtenteEntity> utenti = new ArrayList<>();
        String sql = "SELECT * FROM utenti";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utenti.add(new UtenteEntity(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("data_nascita")
                ));
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return utenti;
    }


}