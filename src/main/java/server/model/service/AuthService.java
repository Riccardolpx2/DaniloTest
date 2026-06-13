package server.model.service;

import server.model.database.AmministratoreDAO;
import server.model.database.UtenteDAO;
import server.model.database.entity.AmministratoreEntity;
import server.model.database.entity.UtenteEntity;

import java.sql.SQLException;

/**
 * Servizio dedicato alla gestione delle operazioni di autenticazione e registrazione.
 * Funge da intermediario tra gli handler di rete (come {@link server.model.network.state.AuthState})
 * e il livello di accesso ai dati (DAO).
 */
public class AuthService {

    private final UtenteDAO utenteDAO;
    private final AmministratoreDAO amministratoreDAO;

    /**
     * Inizializza il servizio istanziando i DAO necessari per l'accesso ai dati
     * degli utenti e degli amministratori.
     */
    public AuthService(){
        this.utenteDAO = new UtenteDAO();
        this.amministratoreDAO = new AmministratoreDAO();
    }

    /**
     * Verifica le credenziali di accesso per un Amministratore.
     *
     * @param username L'username dell'amministratore.
     * @param password La password in chiaro.
     * @return L'oggetto {@link AmministratoreEntity} se le credenziali sono corrette, {@code null} altrimenti.
     * @throws SQLException In caso di errore durante l'interrogazione al database.
     */
    public AmministratoreEntity loginAdmin(String username, String password) throws SQLException {
        AmministratoreEntity admin = amministratoreDAO.cerca(username);
        return (admin != null && admin.getPassword().equals(password)) ? admin : null;
    }


    /**
     * Verifica le credenziali di accesso per un Utente standard (giocatore).
     *
     * @param username L'username dell'utente che tenta il login.
     * @param password La password in chiaro.
     * @return L'oggetto {@link UtenteEntity} se le credenziali sono corrette, {@code null} altrimenti.
     * @throws SQLException In caso di errore durante l'interrogazione al database.
     */
    public UtenteEntity login(String username, String password) throws SQLException {
        UtenteEntity utenteEntity;


        utenteEntity = utenteDAO.cerca(username);

        return (utenteEntity != null && utenteEntity.getPassword().equals(password)) ? utenteEntity : null;
    }


    /**
     * Registra un nuovo utente nel sistema. Verifica preliminarmente che l'username
     * non sia già occupato da un altro giocatore.
     *
     * @param username L'username scelto per il nuovo account.
     * @param password La password scelta per il nuovo account.
     * @param nome Il nome di battesimo dell'utente.
     * @param cognome Il cognome dell'utente.
     * @param dataNascita La data di nascita dell'utente.
     * @return {@code true} se la registrazione avviene con successo, {@code false} se l'username è già in uso.
     * @throws SQLException In caso di errori durante la scrittura sul database.
     */
    public boolean register(String username, String password, String nome, String cognome, String dataNascita) throws SQLException {
        UtenteEntity utenteEntity = new UtenteEntity(username, password, nome, cognome, dataNascita);

        if (utenteDAO.cerca(username)!=null) return false;

        utenteDAO.aggiungi(utenteEntity);

        return true;
    }

}