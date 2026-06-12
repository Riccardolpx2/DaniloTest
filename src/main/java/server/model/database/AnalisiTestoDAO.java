/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import server.gameUtil.AnalisiTesto;

/**
 *
 * @author Utente
 */
public class AnalisiTestoDAO implements DAO<AnalisiTesto,String>{
    
    @Override
    public void aggiungi(AnalisiTesto analisi) throws SQLException {
        String sql = "INSERT INTO analisi_testi (idDocumento, dati_serializzati) VALUES (?, ?);";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, analisi.getIdDocumento());
            
            // Serializzazione dell'oggetto in un array di byte
            byte[] datiPreparati = serializza(analisi);
            pstmt.setBytes(2, datiPreparati);

            pstmt.executeUpdate();
            System.out.println("AnalisiTesto per il documento ID: " + analisi.getIdDocumento() + " salvata con successo.");
        } catch (SQLException e) {
            System.err.println("Errore nell'aggiunta dell'analisi: " + e.getMessage());
            throw e;
        }
    }
    
    @Override 
    public void rimuovi(AnalisiTesto analisi) throws SQLException{
        throw new UnsupportedOperationException("Operazione di rimozione non supportata, quando cancello un Documento si eliminano"
                + "in cascata tutti i pezzi di testo");
    }
    
    @Override 
    public void aggiorna(AnalisiTesto analisi) throws SQLException{
        throw new UnsupportedOperationException("Operazione di aggiornamento non supportata");
    }
    
    @Override 
    public AnalisiTesto cerca(String key) throws SQLException{
        throw new UnsupportedOperationException("Operazione di ricerca non supportata");
    }
   
    @Override
    public List<AnalisiTesto> elencaTutti() throws SQLException {
        String sql = "SELECT dati_serializzati FROM analisi_testi;";
        List<AnalisiTesto> listaAnalisi = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                byte[] datiSerializzati = rs.getBytes("dati_serializzati");
                if (datiSerializzati != null) {
                    AnalisiTesto analisi = deserializza(datiSerializzati);
                    if (analisi != null) {
                        listaAnalisi.add(analisi);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return listaAnalisi;
    }

    private byte[] serializza(AnalisiTesto obj) throws SQLException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            oos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SQLException("Errore critico durante la serializzazione dell'oggetto AnalisiTesto", e);
        }
    }

    private AnalisiTesto deserializza(byte[] byteDati) throws SQLException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(byteDati);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (AnalisiTesto) ois.readObject();
        } catch (Exception e) {
            throw new SQLException("Errore critico durante la deserializzazione dell'oggetto AnalisiTesto", e);
        }
    }
    
}
