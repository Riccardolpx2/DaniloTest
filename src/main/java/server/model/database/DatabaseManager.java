package server.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class  DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:database.db";
    private static Connection conn;

    public static Connection getConnection() throws SQLException {//metodo statico per ottenere connessione
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection(DB_URL);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        return conn;
    }

    public static void closeConnection() throws SQLException {//idem per chiudere
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
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
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "testo TEXT NOT NULL" +
                ");";
 
    String creaTabellaSessioneDiGioco = "CREATE TABLE IF NOT EXISTS sessioni (" +
            "idSessione INTEGER PRIMARY KEY AUTOINCREMENT," +
            "utente1 TEXT NOT NULL," +
            "utente2 TEXT NOT NULL," +
            "data_ora TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "durataSessione INTEGER DEFAULT 0," + 
            "FOREIGN KEY (utente1) REFERENCES utenti(username) ON DELETE CASCADE," +
            "FOREIGN KEY (utente2) REFERENCES utenti(username) ON DELETE CASCADE" +
            ");";        

        String creaTabellaPartita = "CREATE TABLE IF NOT EXISTS partite (" +
            "idPartita INTEGER PRIMARY KEY AUTOINCREMENT," +
            "idSessione INTEGER NOT NULL," +
            "idDocumento INTEGER NOT NULL, " +
            "offsetInizio INTEGER NOT NULL," +
            "lunghezza INTEGER NOT NULL," +
            "shiftCesare INTEGER NOT NULL," +
            "parolaSoluzione TEXT NOT NULL," +
            "secondiRispostaG1 INTEGER NOT NULL," + 
            "secondiRispostaG2 INTEGER NOT NULL," + 
            "punteggioG1 INTEGER DEFAULT 0," +  
            "punteggioG2 INTEGER DEFAULT 0," +  
            "difficolta TEXT NOT NULL," + 
            "vincitore TEXT," +
            "FOREIGN KEY (idSessione) REFERENCES sessioni(idSessione) ON DELETE CASCADE," +
            "FOREIGN KEY (idDocumento) REFERENCES documenti(id) ON DELETE CASCADE," +
            "FOREIGN KEY (vincitore) REFERENCES utenti(username)" +
            ");";
        
        String creaTabellaStatistiche = "CREATE TABLE IF NOT EXISTS statistiche (" +
                "username TEXT PRIMARY KEY, " + 
                "vittorie INTEGER DEFAULT 0, " +
                "sconfitte INTEGER DEFAULT 0, " +
                "percentualeRisposte INTEGER DEFAULT 0,"+
                "mediaRisposta REAL DEFAULT 0.0, " + 
                "FOREIGN KEY (username) REFERENCES utenti(username) ON DELETE CASCADE" +
                ");";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(creaTabellaUtenti);
            System.out.println("Tabella 'utenti' creata o già esistente.");
           
            stmt.execute(creaTabellaAmministratori);
            System.out.println("Tabella 'amministratori' creata o già esistente.");
            
            stmt.execute(creaTabellaDocumenti);
            System.out.println("Tabella 'documenti' creata o già esistente.");
            
            stmt.execute(creaTabellaSessioneDiGioco);
            System.out.println("Tabella 'sessioni' creata o già esistente.");           
            
            stmt.execute(creaTabellaPartita);
            System.out.println("Tabella 'partite' creata o già esistente.");
            
            stmt.execute(creaTabellaStatistiche);
            System.out.println("Tabella 'statistiche' creata o già esistente.");
            
            System.out.println("-> Database inizializzato con successo!");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }


}