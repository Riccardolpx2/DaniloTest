package server.model.database;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T,K> {

    public void aggiungi(T el) throws SQLException;

    public void rimuovi(T el) throws SQLException;

    public void aggiorna(T el) throws SQLException;

    public T cerca(K key) throws SQLException;

    List<T> elencaTutti() throws SQLException;
}