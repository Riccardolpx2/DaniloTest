package server.model.network;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestore Singleton delle sessioni attive sul server.
 * Permette di tenere traccia degli utenti attualmente autenticati per evitare
 * accessi multipli simultanei con le stesse credenziali.
 */
public class SessionManager {
    private static SessionManager instance;
    private final ConcurrentHashMap<String, ClientHandler> activeSessions;

    private SessionManager() {
        activeSessions = new ConcurrentHashMap<>();
    }

    /**
     * Restituisce l'istanza singleton del SessionManager.
     *
     * @return L'istanza singleton.
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Tenta di registrare un login per un determinato utente.
     *
     * @param username L'username dell'utente che sta effettuando il login.
     * @param handler  L'handler del client associato.
     * @return {@code true} se il login ha successo (utente non era già loggato), {@code false} altrimenti.
     */
    public boolean login(String username, ClientHandler handler) {
        return activeSessions.putIfAbsent(username, handler) == null;
    }

    /**
     * Rimuove un utente dalle sessioni attive (logout).
     *
     * @param username L'username dell'utente da disconnettere.
     */
    public void logout(String username) {
        if (username != null) {
            activeSessions.remove(username);
        }
    }
}