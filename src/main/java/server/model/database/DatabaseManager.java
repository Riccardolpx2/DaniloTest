package server.model.database;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestore centralizzato del ciclo di vita del Database.
 * Fornisce i metodi statici per ottenere connessioni JDBC al database SQLite,
 * si occupa dell'inizializzazione automatica dello schema relazionale all'avvio del server
 * e offre funzionalità native per il backup e il ripristino dei dati.
 */
public class  DatabaseManager {
    
    /** Percorso di connessione al database SQLite locale (file fisico del DB) */
    private static final String DB_URL = "jdbc:sqlite:" + Paths.get("db", "database.db").toString();
    
    /**
     * Apre e restituisce una connessione attiva verso il database SQLite.
     * Carica dinamicamente il driver JDBC e forza l'attivazione del supporto alle Foreign Key 
     * tramite il comando specifico di SQLite 'PRAGMA foreign_keys = ON;'.
     * @return Un oggetto {@link Connection} valido e configurato.
     * @throws SQLException Se il driver non viene trovato o se fallisce lo stabilimento della connessione.
     */
    public static Connection getConnection() throws SQLException {//metodo statico per ottenere connessione
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            throw new SQLException("Driver SQLite non trovato", e);
            }
        
        Connection conn = DriverManager.getConnection(DB_URL);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        return conn;
    }
    /**
     * Inizializza l'intero schema del database creando le tabelle necessarie al funzionamento 
     * del gioco qualora non fossero già presenti nel file di persistenza.
     * Le tabelle generate includono:
     * utenti: Anagrafica e credenziali dei giocatori.
     * amministratori: Credenziali per il pannello di gestione.
     * documenti: Testi in chiaro sorgente.
     * partite: Storico dei match di gioco.
     * partite_tempi_round: Dettaglio dei tempi spesi round per round (chiave composta).
     * statistiche: Rendimento storico dei singoli utenti.
     * domande: Testi cifrati associati ai documenti.
     * analisi_parole: Mappa delle frequenze delle parole estratte (chiave composta).
     * I vincoli di eliminazione sono configurati prevalentemente in modalità ON DELETE CASCADE.
     */
    public static void inizializzaDatabase() {
        String creaTabellaUtenti = "CREATE TABLE IF NOT EXISTS utenti (" +
                "username TEXT PRIMARY KEY," +
                "password TEXT NOT NULL," +
                "nome TEXT NOT NULL," +
                "cognome TEXT NOT NULL," +
                "data_nascita TEXT" +
                ");";

        String creaTabellaAmministratori = "CREATE TABLE IF NOT EXISTS amministratori (" +
                "username TEXT PRIMARY KEY," +
                "password TEXT NOT NULL" +
                ");";
        
        String creaTabellaDocumenti = "CREATE TABLE IF NOT EXISTS documenti (" +
                "idDocumento INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "testo TEXT NOT NULL" +
                ");";
        

        String creaTabellaPartita = "CREATE TABLE IF NOT EXISTS partite (" +
                "idPartita INTEGER PRIMARY KEY AUTOINCREMENT," +
                "dataInizio TIMESTAMP NOT NULL," +
                "durataPartita INTEGER DEFAULT 0," +
                "stato TEXT NOT NULL," +
                "player1_username TEXT NOT NULL," +
                "player2_username TEXT NOT NULL," +
                "vincitore_username TEXT," +
                "punteggioTotaleG1 INTEGER DEFAULT 0," + 
                "punteggioTotaleG2 INTEGER DEFAULT 0," + 
                "FOREIGN KEY (player1_username) REFERENCES utenti(username) ON DELETE CASCADE," +
                "FOREIGN KEY (player2_username) REFERENCES utenti(username) ON DELETE CASCADE," +
                "FOREIGN KEY (vincitore_username) REFERENCES utenti(username)" +
                ");";
        
        String creaTabellaTempiRound = "CREATE TABLE IF NOT EXISTS partite_tempi_round (" +
                "idPartita INTEGER NOT NULL," +
                "numero_round INTEGER NOT NULL," +
                "tempo_g1 INTEGER NOT NULL," +
                "tempo_g2 INTEGER NOT NULL," +
                "PRIMARY KEY (idPartita, numero_round)," +
                "FOREIGN KEY (idPartita) REFERENCES partite(idPartita) ON DELETE CASCADE" +
                ");";
        
        String creaTabellaStatistiche = "CREATE TABLE IF NOT EXISTS statistiche (" +
                "username TEXT PRIMARY KEY, " + 
                "vittorie INTEGER DEFAULT 0, " +
                "sconfitte INTEGER DEFAULT 0, " +
                "percentualeVittorie INTEGER DEFAULT 0,"+
                "mediaRisposta REAL DEFAULT 0.0, " + 
                "FOREIGN KEY (username) REFERENCES utenti(username) ON DELETE CASCADE" +
                ");";
        

        String creaTabellaDomande = "CREATE TABLE IF NOT EXISTS domande (" +
                "idDomanda INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idDocumento INTEGER NOT NULL, " +
                "difficolta TEXT NOT NULL, " +
                "testoCifrato TEXT NOT NULL, " +
                "paroleSoluzioni TEXT NOT NULL, " + // Conterrà le parole in chiaro separate da virgola
                "paroleCifrate TEXT NOT NULL, " +   // Conterrà le parole cifrate separate da virgola
                "FOREIGN KEY (idDocumento) REFERENCES documenti(idDocumento) ON DELETE CASCADE" +
                ");";
                
                String creaTabellaDettaglioParole = "CREATE TABLE IF NOT EXISTS analisi_parole (" +
                "idDocumento INTEGER NOT NULL," +
                "parola TEXT NOT NULL," +
                "frequenza INTEGER NOT NULL," +
                "PRIMARY KEY (idDocumento, parola)," +
                "FOREIGN KEY (idDocumento) REFERENCES documenti(idDocumento) ON DELETE CASCADE" +
                ");";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(creaTabellaUtenti);
            System.out.println("Tabella 'utenti' creata o già esistente.");
           
            stmt.execute(creaTabellaAmministratori);
            System.out.println("Tabella 'amministratori' creata o già esistente.");
            
            stmt.execute(creaTabellaDocumenti);
            System.out.println("Tabella 'documenti' creata o già esistente.");
            
            stmt.execute(creaTabellaPartita);
            System.out.println("Tabella 'partite' creata o già esistente.");
            
            stmt.execute(creaTabellaTempiRound);
            System.out.println("Tabella 'partite_tempi_round' creata o già esistente.");
            
            stmt.execute(creaTabellaStatistiche);
            System.out.println("Tabella 'statistiche' creata o già esistente.");
            
            stmt.execute(creaTabellaDomande);
            System.out.println("Tabella 'domande' creata o già esistente.");
 
            stmt.execute(creaTabellaDettaglioParole);
            System.out.println("Tabella 'analisi_parole' creata o già esistente.");
            
            System.out.println("-> Database inizializzato con successo!");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    /**
     * Esegue una copia di backup a caldo  dell'intero stato corrente del database
     * salvandola nel percorso file specificato. Sfrutta il comando nativo SQLite 'BACKUP TO'.
     * @param filePath Il percorso assoluto o relativo del file di destinazione (es. "backup.db").
     * @throws SQLException Se si verificano errori di I/O o di blocco durante la copia dei blocchi di memoria.
     */
    public static void eseguiBackup(String filePath) throws SQLException{
        try (Connection connection = getConnection();
        Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("BACKUP TO '" + filePath +"'");
        }
    }
    /**
     * Ripristina integralmente lo stato del database partendo da un file di backup precedentemente creato.
     * Sfrutta il comando nativo SQLite 'RESTORE FROM'.
     * Questa operazione sovrascrive completamente tutti i dati correnti in memoria.
     * @param filePath Il percorso del file di backup da cui attingere i dati.
     * @throws SQLException Se il file non è valido, è corrotto o se fallisce il ripristino delle tabelle.
     */
    public static void eseguiRestore(String filePath) throws SQLException{
        try(Connection connection = getConnection();
        Statement stmt = connection.createStatement()){
            stmt.executeUpdate("RESTORE FROM '" + filePath + "'");
        }
    }

}
