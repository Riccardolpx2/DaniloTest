package server.model.network;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static SessionManager instance;
    private final ConcurrentHashMap<String, ClientHandler> activeSessions;

    private SessionManager() {
        activeSessions = new ConcurrentHashMap<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public boolean login(String username, ClientHandler handler) {
        return activeSessions.putIfAbsent(username, handler) == null;
    }

    public void logout(String username) {
        if (username != null) {
            activeSessions.remove(username);
        }
    }
}