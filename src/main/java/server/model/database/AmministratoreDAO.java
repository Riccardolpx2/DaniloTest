package server.model.database;

import client.network.ConnectionHandler;
import server.model.database.entity.AmministratoreEntity;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AmministratoreDAO implements DAO<AmministratoreEntity, String> {

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

    @Override
    public void rimuovi(AmministratoreEntity el) throws SQLException {
        String sql = "DELETE FROM amministratori WHERE usernam = ?";
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

    @Override
    public List<AmministratoreEntity> elencaTutti() throws SQLException {
        List<AmministratoreEntity> amministratori = new ArrayList<>();
        String sql = "SELECT FROM * amministratori";

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
