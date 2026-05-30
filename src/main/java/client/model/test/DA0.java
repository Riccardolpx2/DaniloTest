package client.model.test;

import java.sql.SQLException;
import java.util.List;

public interface DA0 <T> {

    public void aggiungi(T el) throws SQLException;

    public void rimuovi(T el) throws Exception;

    public void aggiorna(T el) throws Exception;

    public T cerca(String key) throws Exception;

    List<T> elencaTutti() throws Exception;
}