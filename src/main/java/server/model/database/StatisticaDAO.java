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
import server.model.database.entity.UtenteEntity;
import server.gameUtil.Statistica;

/**
 *
 * @author Utente
 */
public class StatisticaDAO implements DAO<Statistica,String>{

    @Override
    public void aggiungi(Statistica st) throws SQLException{
        String sql = "INSERT INTO statistiche (username, vittorie, sconfitte, percentualeVittorie, mediaRisposta) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, st.getPlayer().getUsername());
            pstmt.setInt(2, st.getVittorie());
            pstmt.setInt(3, st.getSconfitte());
            pstmt.setInt(4, st.getPercentualeVittorie());
            pstmt.setDouble(5, st.getMediaRisposta());

            pstmt.executeUpdate();
            System.out.println("Statistica inserita con successo per l'utente: " + st.getPlayer().getUsername());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }

    }

    @Override
    public void rimuovi(Statistica st) throws SQLException{
        throw new UnsupportedOperationException("Operazione di rimozione non supportata per le statistiche delle partite."
                + " Se elimini l'utente verranno eliminate le statistiche in cascata");
    }

    @Override
    public void aggiorna(Statistica st) throws SQLException{
    String sql = "UPDATE statistiche SET vittorie = ?, sconfitte = ?, percentualeVittorie = ?, mediaRisposta = ? WHERE username = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, st.getVittorie());
            pstmt.setInt(2, st.getSconfitte());
            pstmt.setInt(3, st.getPercentualeVittorie());
            pstmt.setDouble(4, st.getMediaRisposta());
            pstmt.setString(5, st.getPlayer().getUsername());

            int righeModificate = pstmt.executeUpdate();
            System.out.println("Statistica aggiornata. Righe modificate: " + righeModificate);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public Statistica cerca(String key) throws SQLException{
        String sql = "SELECT * FROM statistiche WHERE username = ?;";
        Statistica st = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int vittorie = rs.getInt("vittorie");
                    int sconfitte = rs.getInt("sconfitte");
                    int percentuale = rs.getInt("percentualeVittorie");
                    double media = rs.getDouble("mediaRisposta");
                    UtenteEntity player = new UtenteEntity(key, null, null, null, null);

                    st = new Statistica(player, vittorie, sconfitte, percentuale, media);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return st;
    }

    @Override
    public List<Statistica> elencaTutti() throws SQLException{
        String sql = "SELECT * FROM statistiche;";
        List<Statistica> lista = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                int vittorie = rs.getInt("vittorie");
                int sconfitte = rs.getInt("sconfitte");
                int percentuale = rs.getInt("percentualeVittorie");
                double media = rs.getDouble("mediaRisposta");

                UtenteEntity player = new UtenteEntity(username, null, null, null, null);
                Statistica st = new Statistica(player, vittorie, sconfitte, percentuale, media);

                lista.add(st);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return lista;
    }
}


