package server.model.database;

import shared.model.Utente;
import shared.model.UtenteLogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO implements DAO<Utente> {

    @Override
    public void aggiungi(Utente el) throws SQLException {
        String sql = "INSERT INTO utenti(username, password, nome, cognome, data_nascita) VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, el.getUsername());
            pstmt.setString(2, el.getPassword());
            pstmt.setString(3, el.getNome());
            pstmt.setString(4, el.getCognome());
            pstmt.setString(5, el.getDataNascita());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void rimuovi(Utente el) throws Exception {
        String sql = "DELETE FROM utenti WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, el.getUsername());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void aggiorna(Utente el) throws Exception {
        String sql = "UPDATE utenti SET password = ?, nome = ?, cognome = ?, data_nascita = ? WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, el.getPassword());
            pstmt.setString(2, el.getNome());
            pstmt.setString(3, el.getCognome());
            pstmt.setString(4, el.getDataNascita());
            pstmt.setString(5, el.getUsername());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Utente cerca(String key) throws Exception {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, key);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("data_nascita")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Utente> elencaTutti() throws Exception {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT * FROM utenti";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utenti.add(new Utente(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("data_nascita")
                ));
            }
        }
        return utenti;
    }

    public boolean login(UtenteLogin credenziali) {
        String sql = "SELECT password FROM utenti WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, credenziali.getUsername());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String passwordSalvata = rs.getString("password");
                    return passwordSalvata.equals(credenziali.getPassword());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}