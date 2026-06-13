package server.model.database;


import server.model.database.entity.AmministratoreEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestione della persistenza degli amministratori di sistema.
 * Implementa l'interfaccia generica {@link DAO} mappando gli oggetti {@link AmministratoreEntity}
 * sulla tabella amministratori, utilizzando lo username dell'amministratore (String) come chiave primaria.
 */
public class AmministratoreDAO implements DAO<AmministratoreEntity, String> {
    
    /**
     * Inserisce un nuovo account amministratore all'interno del database.
     * @param el L'oggetto AmministratoreEntity contenente le credenziali da salvare.
     * @throws SQLException Se lo username è già presente (violazione della PRIMARY KEY)
     * o se si verificano errori di comunicazione con il database.
     */
    @Override
    public void aggiungi(AmministratoreEntity el) throws SQLException {
        String sql = "INSERT INTO amministratori(username, password) VALUES (?,?)";
        try(
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                )
        {
            pstmt.setString(1, el.getUsername());
            pstmt.setString(2, el.getPassword());
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw e;
        }
    }
    /**
     * Rimuove un amministratore dal database partendo dal suo username.
     * @param el L'oggetto AmministratoreEntity da rimuovere (identificato tramite lo username).
     * @throws SQLException Se si verificano anomalie o errori durante l'esecuzione della query di eliminazione.
     */
    @Override
    public void rimuovi(AmministratoreEntity el) throws SQLException {
        String sql = "DELETE FROM amministratori WHERE username = ?";
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                )
        {
            pstmt.setString(1, el.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw e;
        }

    }
    
    /**
     * Aggiorna la password di un amministratore già esistente nel database.
     * Lo username viene utilizzato come criterio di ricerca nella clausola WHERE e rimane immutato.
     * @param el L'oggetto AmministratoreEntity modificato in memoria da sincronizzare sul database.
     * @throws SQLException In caso di problemi di comunicazione o fallimento dell'aggiornamento SQL.
     */
    @Override
    public void aggiorna(AmministratoreEntity el) throws SQLException {
        String sql = "UPDATE amministratori SET password = ? where username = ?";
        try(
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                )
        {
            pstmt.setString(1, el.getPassword());
            pstmt.setString(2, el.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw e;
        }

    }
    
    /**
     * Cerca ed estrae un amministratore specifico dal database tramite il suo username (chiave primaria).
     *@param key Lo username dell'amministratore da cercare.
     * @return Un'istanza di {@link AmministratoreEntity} interamente popolata se trovata,
     * oppure null se lo username non corrisponde ad alcun record.
     * @throws SQLException Se si verificano errori nella lettura del ResultSet o di connessione.
     */
    @Override
    public AmministratoreEntity cerca(String key) throws SQLException {
        String sql = "SELECT * FROM amministratori WHERE username = ?";
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                )
        {
            pstmt.setString(1, key);

            try (ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return new AmministratoreEntity(rs.getString("username"), rs.getString("password"));
                }
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw e;
        }
        return null;
    }
    /**
     * Recupera la lista completa di tutti gli amministratori censiti all'interno del sistema.
     * @return Una {@link List} contenente tutti gli oggetti AmministratoreEntity trovati.
     * Se la tabella è vuota, restituisce una lista vuota.
     * @throws SQLException In caso di errori durante l'estrazione massiva dei record.
     */
    @Override
    public List<AmministratoreEntity> elencaTutti() throws SQLException {
        List<AmministratoreEntity> amministratori = new ArrayList<>();
        String sql = "SELECT * FROM amministratori";

        try (
                Connection conn = DatabaseManager.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(sql);
        ){
            while (rs.next()){
                amministratori.add(new AmministratoreEntity(
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw e;
        }
        return amministratori;
    }
}
