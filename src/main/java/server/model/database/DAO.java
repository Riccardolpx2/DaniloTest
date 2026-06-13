package server.model.database;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaccia generica che definisce il contratto standard per le operazioni CRUD 
 * (Create, Read, Update, Delete) verso il Database relazionale.
 * Segue il Data Access Object (DAO) Pattern per separare la logica di business 
 * del server dallo strato di persistenza dei dati.
 * @param <T> Il tipo dell'Oggetto di Dominio / Entity gestito dal DAO (es. UtenteEntity, PartitaEntity).
 * @param <K> Il tipo della Chiave Primaria utilizzata per identificare l'entità nel DB (es. String per username, Integer per ID).
 * @author Utente
 */
public interface DAO<T,K> {

    public void aggiungi(T el) throws SQLException;

    public void rimuovi(T el) throws SQLException;

    public void aggiorna(T el) throws SQLException;

    public T cerca(K key) throws SQLException;

    List<T> elencaTutti() throws SQLException;
}