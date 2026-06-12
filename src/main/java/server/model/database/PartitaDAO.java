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
import server.gameUtil.Documento;
import server.gameUtil.Partita;

/**
 *
 * @author Utente
 */
public class PartitaDAO implements DAO<Partita,Integer>{
    
@Override
    public void aggiungi(Partita p) throws SQLException {
        // Query principale: salva i dati generali della sfida
        String sql = "INSERT INTO partite (dataInizio, durataPartita, stato, player1_username, player2_username, "
                   + "vincitore_username, punteggioTotaleG1, punteggioTotaleG2) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(p.getDataInizio()));
            pstmt.setInt(2, p.getDurataPartita());
            pstmt.setString(3, p.getStato());
            pstmt.setString(4, p.getPlayer1().getUsername());
            pstmt.setString(5, p.getPlayer2().getUsername());
            
            if (p.getVincitore() != null) {
                pstmt.setString(6, p.getVincitore().getUsername());
            } else {
                pstmt.setNull(6, java.sql.Types.VARCHAR); // Gestione del pareggio
            }
            
            pstmt.setInt(7, p.getPunteggioTotaleG1());
            pstmt.setInt(8, p.getPunteggioTotaleG2());

            pstmt.executeUpdate();

            // Recuperiamo l'ID generato automaticamente dal Database per associarlo all'oggetto
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    p.setIdPartita(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Partita inserita nel DB con successo! ID assegnato: " + p.getIdPartita());

            // --- SALVATAGGIO DEI TEMPI DI RISPOSTA DEI ROUND ---
            // Sfruttiamo l'ID appena ottenuto per salvare tutti i tempi storici accumulati nelle liste
            salvaTempiRound(conn, p);

        } catch (SQLException e) {
            System.err.println("Errore nell'inserimento della partita: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Metodo di supporto privato che effettua l'inserimento batch dei tempi dei singoli round 
     * sfruttando la stessa connessione della transazione principale.
     */
    private void salvaTempiRound(Connection conn, Partita p) throws SQLException {
        String sqlRound = "INSERT INTO partite_tempi_round (idPartita, numero_round, tempo_g1, tempo_g2) VALUES (?, ?, ?, ?)";
        
        List<Integer> tempiG1 = p.getTempiRispostaG1();
        List<Integer> tempiG2 = p.getTempiRispostaG2();

        try (PreparedStatement pstmtRound = conn.prepareStatement(sqlRound)) {
            // Cicliamo sulla dimensione degli array dei tempi accumulati in RAM
            for (int i = 0; i < tempiG1.size(); i++) {
                pstmtRound.setInt(1, p.getIdPartita());
                pstmtRound.setInt(2, i + 1); // Contatore del round (Round 1, Round 2, ecc.)
                pstmtRound.setInt(3, tempiG1.get(i));
                pstmtRound.setInt(4, tempiG2.get(i));
                
                pstmtRound.addBatch(); // Prepariamo l'inserimento in blocco (Batch)
            }
            
            pstmtRound.executeBatch(); // Spediamo tutti i record dei round in un colpo solo
            System.out.println("Salvati " + tempiG1.size() + " round nel database per la partita " + p.getIdPartita());
        }
    }

    @Override
    public void rimuovi(Partita p) throws SQLException {
        throw new UnsupportedOperationException("Operazione di rimozione non supportata per la cronologia delle partite.");
    }

    @Override
    public void aggiorna(Partita p) throws SQLException {
        throw new UnsupportedOperationException("Operazione di aggiornamento non supportata. Le partite storiche sono immutabili.");
    }

    @Override
    public Partita cerca(Integer key) throws SQLException {
        String sql = "SELECT * FROM partite WHERE idPartita = ?";
        Partita p = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, key);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idPartita");
                    java.time.LocalDateTime inizio = rs.getTimestamp("dataInizio").toLocalDateTime();
                    int durata = rs.getInt("durataPartita");
                    String stato = rs.getString("stato");
                    int puntiG1 = rs.getInt("punteggioTotaleG1");
                    int puntiG2 = rs.getInt("punteggioTotaleG2");
                    
                    UtenteEntity p1 = new UtenteEntity(rs.getString("player1_username"), null, null, null, null);
                    UtenteEntity p2 = new UtenteEntity(rs.getString("player2_username"), null, null, null, null);
                    
                    UtenteEntity vincitore = null;
                    String usernameVincitore = rs.getString("vincitore_username");
                    if (usernameVincitore != null) {
                        vincitore = new UtenteEntity(usernameVincitore, null, null, null, null);
                    }

                    // Utilizziamo il costruttore completo che hai scritto tu in Partita
                    p = new Partita(id, inizio, durata, stato, p1, p2, vincitore, puntiG1, puntiG2);
                    
                    // Recuperiamo anche l'array dei tempi storici di questa specifica partita dal DB
                    recuperaTempiRound(conn, p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore nella ricerca della partita: " + e.getMessage());
            throw e;
        }
        return p;
    }

    /**
     * Metodo di supporto privato che ripopola gli array di tempi rispostaG1 e rispostaG2 
     * quando viene estratta una partita passata.
     */
    private void recuperaTempiRound(Connection conn, Partita p) throws SQLException {
        String sql = "SELECT tempo_g1, tempo_g2 FROM partite_tempi_round WHERE idPartita = ? ORDER BY numero_round ASC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getIdPartita());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Ripopoliamo le liste native richiamando il metodo che hai inserito in Partita
                    p.registraTempiRound(rs.getInt("tempo_g1"), rs.getInt("tempo_g2"));
                }
            }
        }
    }

    @Override
    public List<Partita> elencaTutti() throws SQLException {
        String sql = "SELECT * FROM partite";
        List<Partita> listaPartite = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idPartita");
                java.time.LocalDateTime inizio = rs.getTimestamp("dataInizio").toLocalDateTime();
                int durata = rs.getInt("durataPartita");
                String stato = rs.getString("stato");
                int puntiG1 = rs.getInt("punteggioTotaleG1");
                int puntiG2 = rs.getInt("punteggioTotaleG2");
                
                UtenteEntity p1 = new UtenteEntity(rs.getString("player1_username"), null, null, null, null);
                UtenteEntity p2 = new UtenteEntity(rs.getString("player2_username"), null, null, null, null);
                
                UtenteEntity vincitore = null;
                String usernameVincitore = rs.getString("vincitore_username");
                if (usernameVincitore != null) {
                    vincitore = new UtenteEntity(usernameVincitore, null, null, null, null);
                }

                Partita p = new Partita(id, inizio, durata, stato, p1, p2, vincitore, puntiG1, puntiG2);
                
                // Opzionale: se volete che l'elenco completo si tiri dietro anche tutti gli array di tempi:
                // recuperaTempiRound(conn, p);
                
                listaPartite.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Errore nell'elencare le partite: " + e.getMessage());
            throw e;
        }
        return listaPartite;
    }   
}
