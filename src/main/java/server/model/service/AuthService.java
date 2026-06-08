package server.model.service;

import server.model.database.AmministratoreDAO;
import server.model.database.UtenteDAO;
import server.model.database.entity.AmministratoreEntity;
import server.model.database.entity.UtenteEntity;

import java.sql.SQLException;

public class AuthService {

    private final UtenteDAO utenteDAO;
    private final AmministratoreDAO amministratoreDAO;

    public AuthService(){
        this.utenteDAO = new UtenteDAO();
        this.amministratoreDAO = new AmministratoreDAO();
    }

    public AmministratoreEntity loginAdmin(String username, String password) throws SQLException {
        AmministratoreEntity admin = amministratoreDAO.cerca(username);
        return (admin != null && admin.getPassword().equals(password)) ? admin : null;
    }


    // metodo per verificare la login sul server
    public UtenteEntity login(String username, String password) throws SQLException {
        UtenteEntity utenteEntity;


        utenteEntity = utenteDAO.cerca(username);

        return (utenteEntity != null && utenteEntity.getPassword().equals(password)) ? utenteEntity : null;
    }


    public boolean register(String username, String password, String nome, String cognome, String dataNascita) throws SQLException {
        UtenteEntity utenteEntity = new UtenteEntity(username, password, nome, cognome, dataNascita);

        if (utenteDAO.cerca(username)!=null) return false;

        utenteDAO.aggiungi(utenteEntity);

        return true;
    }

}