package server.model.database;

import server.model.database.entity.UtenteEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO implements DAO<UtenteEntity,String> {

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