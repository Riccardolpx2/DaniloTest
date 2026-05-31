package server.service;

import server.model.database.DatabaseManager;
import server.model.database.UtenteDAO;
import shared.model.Utente;
import shared.model.UtenteLogin;

public class AuthService {

    /*
    metodo per verificare la login sul server
     */
    public static boolean authenticate(String username, String password) {
        UtenteLogin ul = new UtenteLogin(username,password);
        UtenteDAO u = new UtenteDAO();

        return u.login(ul);
    }


    public static boolean register(Utente u){
        UtenteDAO ud = new UtenteDAO();
        try{
            ud.aggiungi(u);
            System.out.println("Utente aggiunto al db");
            return  true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}