/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import server.model.database.entity.PartitaEntity;
import server.model.database.entity.UtenteEntity;

/**
 * Gestione della persistenza delle partite.
 * Mappa gli oggetti {@link PartitaEntity} su uno schema a due tabelle correlate partite e
 * partite_tempi_round per tracciare sia i dati riassuntivi del match sia il dettaglio 
 * dei tempi di risposta round per round.
 * @author Utente
 */
public class PartitaDAO implements DAO<PartitaEntity,Integer>{
/**
     * Inserisce una nuova partita nel database relazionale.
     * Salva prima i dati generali del match recuperando l'ID autoincrementale generato e, 
     * successivamente, invoca il salvataggio in modalità batch dei tempi di risposta 
     * associati ai singoli round.
     * @param p L'oggetto PartitaEntity contenente i dati correnti del match concluso o in corso.
     * @throws SQLException Se si verifica un errore durante l'inserimento principale o nel blocco batch dei round.
     */    
@Override
    public void aggiungi(PartitaEntity p) throws SQLException {
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
                // Gestione del pareggio
                pstmt.setNull(6, java.sql.Types.VARCHAR); 
            }
            
            pstmt.setInt(7, p.getPunteggioTotaleG1());
            pstmt.setInt(8, p.getPunteggioTotaleG2());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    p.setIdPartita(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("PartitaEntity inserita nel DB con successo! ID assegnato: " + p.getIdPartita());

            // Sfruttiamo l'ID appena ottenuto per salvare tutti i tempi storici accumulati nelle liste
            salvaTempiRound(conn, p);

        } catch (SQLException e) {
            System.out.println( e.getMessage());
            throw e;
        }
    }

    /**
     * Metodo di supporto privato che effettua l'inserimento batch dei tempi dei singoli round.
     * Ottimizza le performance inviando i record in un unico blocco.
     * @param conn La connessione SQL attiva ereditata dal metodo chiamante.
     * @param p La partita da cui estrarre le liste dei tempi di risposta dei giocatori.
     * @throws SQLException Se l'esecuzione del batch fallisce.
     */
    private void salvaTempiRound(Connection conn, PartitaEntity p) throws SQLException {
        String sqlRound = "INSERT INTO partite_tempi_round (idPartita, numero_round, tempo_g1, tempo_g2) VALUES (?, ?, ?, ?)";
        
        List<Integer> tempiG1 = p.getTempiRispostaG1();
        List<Integer> tempiG2 = p.getTempiRispostaG2();

        try (PreparedStatement pstmtRound = conn.prepareStatement(sqlRound)) {
            for (int i = 0; i < tempiG1.size(); i++) {
                pstmtRound.setInt(1, p.getIdPartita());
                pstmtRound.setInt(2, i + 1); 
                pstmtRound.setInt(3, tempiG1.get(i));
                pstmtRound.setInt(4, tempiG2.get(i));
                // Prepariamo l'inserimento in blocco (Batch)
                pstmtRound.addBatch(); 
            }
            // Spediamo tutti i record dei round in un colpo solo
            pstmtRound.executeBatch(); 
            System.out.println("Salvati " + tempiG1.size() + " round nel database per la partita " + p.getIdPartita());
        }
    }
    /**
     * Operazione non supportata. La rimozione della cronologia delle partite non è consentita per preservare l'integrità dei dati storici.
     * * @throws UnsupportedOperationException Sempre.
     */
    @Override
    public void rimuovi(PartitaEntity p) throws SQLException {
        throw new UnsupportedOperationException("Operazione di rimozione non supportata per la cronologia delle partite.");
    }
    
    /**
     * Operazione non supportata. I record delle partite storiche sono considerati immutabili per scopi statistici.
     * * @throws UnsupportedOperationException Sempre.
     */
    @Override
    public void aggiorna(PartitaEntity p) throws SQLException {
        throw new UnsupportedOperationException("Operazione di aggiornamento non supportata. Le partite storiche sono immutabili.");
    }
    
    /**
     * Ricerca e ricostruisce una partita passata partendo dal suo ID identificativo.
     * Esegue in modo sequenziale il recupero dei dati macro della partita e il ripopolamento 
     * delle liste interne relative ai tempi dei round.
     * @param key L'ID numerico della partita da cercare.
     * @return L'oggetto {@link PartitaEntity} interamente ricostruito, oppure null se l'ID non corrisponde ad alcun record.
     * @throws SQLException In caso di anomalie di lettura o conversione dati.
     */
    @Override
    public PartitaEntity cerca(Integer key) throws SQLException {
        String sql = "SELECT * FROM partite WHERE idPartita = ?";
        PartitaEntity p = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, key);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idPartita");
                    LocalDateTime inizio = rs.getTimestamp("dataInizio").toLocalDateTime();
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

                    p = new PartitaEntity(id, inizio, durata, stato, p1, p2, vincitore, puntiG1, puntiG2);
                    
                    recuperaTempiRound(conn, p);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return p;
    }

    /**
     * Metodo di supporto privato che ripopola gli array dei tempi di risposta di una partita 
     * recuperando i record dalla tabella dipendente, ordinati in ordine cronologico di round.
     * @param conn La connessione SQL attiva ereditata dal metodo chiamante.
     * @param p L'istanza di partita da ripopolare.
     * @throws SQLException Se si verificano errori nella query di selezione dei round.
     */ 
    private void recuperaTempiRound(Connection conn, PartitaEntity p) throws SQLException {
        String sql = "SELECT tempo_g1, tempo_g2 FROM partite_tempi_round WHERE idPartita = ? ORDER BY numero_round ASC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getIdPartita());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Ripopoliamo le liste native richiamando il metodo che hai inserito in PartitaEntity
                    p.registraTempiRound(rs.getInt("tempo_g1"), rs.getInt("tempo_g2"));
                }
            }
        }
    }

    /**
     * Estrae l'elenco completo di tutte le partite registrate a sistema.
     * @return Una List contenente tutte le istanze di PartitaEntity memorizzate.
     * @throws SQLException In caso di errore di lettura massiva.
     */
    @Override
    public List<PartitaEntity> elencaTutti() throws SQLException {
        String sql = "SELECT * FROM partite";
        List<PartitaEntity> listaPartite = new ArrayList<>();

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

                PartitaEntity p = new PartitaEntity(id, inizio, durata, stato, p1, p2, vincitore, puntiG1, puntiG2);
                
                //prendiamo anche gli arry dei tempi
                recuperaTempiRound(conn, p);
                
                listaPartite.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return listaPartite;
    }   
}
