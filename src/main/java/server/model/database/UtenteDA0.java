package server.model.database;

import shared.model.Utente;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class UtenteDA0 implements DA0<Utente>{

    @Override
    public void aggiungi(Utente el) throws SQLException {

    }

    @Override
    public void rimuovi(Utente el) throws Exception {

    }

    @Override
    public void aggiorna(Utente el) throws Exception {

    }

    @Override
    public Utente cerca(String key) throws Exception {
        return null;
    }

    @Override
    public List<Utente> elencaTutti() throws Exception {
        return Collections.emptyList();
    }
}
