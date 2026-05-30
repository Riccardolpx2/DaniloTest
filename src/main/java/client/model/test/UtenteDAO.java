package client.model.test;

import shared.model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class UtenteDAO implements DA0<Utente> {

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

    }

    @Override
    public void aggiorna(Utente el) throws Exception {

    }

    @Override
    public Utente cerca(String key) throws Exception {
        return null;
    }

    @Override
    public List<Utente> elencaTutti() throws Exception {
        return Collections.emptyList();
    }
}
