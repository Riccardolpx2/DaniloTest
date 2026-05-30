package client;


import server.model.database.UtenteDAO;

public class Main {
    public static void main(String[] args) throws Exception {

        UtenteDAO u = new UtenteDAO();
        System.out.println(u.cerca("PeloneTostoEDuro"));
    }
}
