package server.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class  DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:database.db";

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

    public static void eseguiBackup(String filePath) throws SQLException{
        try (Connection connection = getConnection();
        Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("BACKUP TO '" + filePath +"'");
        }
    }

    public static void eseguiRestore(String filePath) throws SQLException{
        try(Connection connection = getConnection();
        Statement stmt = connection.createStatement()){
            stmt.executeUpdate("RESTORE FROM '" + filePath + "'");
        }
    }

}
