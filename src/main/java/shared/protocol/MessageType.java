package shared.protocol;

import java.io.Serializable;

/**
 * Enumera tutti i possibili tipi di messaggi scambiati attraverso i Socket.
 * Permette un corretto instradamento logico dei messaggi ai metodi preposti 
 * per la loro gestione sia sul Client che sul Server.
 */
public enum MessageType implements Serializable {

    // ==========================================
    //         MESSAGGI DI AUTENTICAZIONE
    // ==========================================

    /**
     * [Client -> Server]
     * Richiesta di accesso. Il payload contiene le credenziali (es. username e password).
     */
    LOGIN_REQUEST,

    /**
     * [Server -> Client]
     * Risposta positiva al login. Indica che l'autenticazione è andata a buon fine.
     */
    LOGIN_SUCCESS,

    /**
     * [Server -> Client]
     * Risposta negativa al login. Il payload contiene i dettagli dell'errore.
     */
    LOGIN_FAILURE,

    /**
     * [Client -> Server]
     * Richiesta di registrazione di un nuovo account. Il payload contiene i dati dell'utente.
     */
    REGISTER_REQUEST,

    /**
     * [Server -> Client]
     * Risposta positiva alla registrazione. L'account è stato creato con successo.
     */
    REGISTER_SUCCESS,

    /**
     * [Server -> Client]
     * Risposta negativa alla registrazione (es. username già in uso, password debole).
     */
    REGISTER_FAILURE,


    // ==========================================
    //           MESSAGGI DI DASHBOARD
    // ==========================================

    /**
     * [Client -> Server]
     * Richiesta di inserimento nella coda di matchmaking per cercare una partita.
     */
    GAME_SEARCH_REQUEST,

    /**
     * [Client -> Server]
     * Richiesta di annullamento della ricerca partita attualmente in corso.
     */
    GAME_SEARCH_CANCEL,

    /**
     * [Server -> Client]
     * Segnala l'inizio della partita. Il payload contiene le informazioni iniziali (es. sfidante, difficoltà).
     */
    GAME_START,

    /**
     * [Server -> Client]
     * Segnala un errore verificatosi durante la ricerca della partita.
     */
    GAME_SEARCH_ERROR,

    /**
     * [Client -> Server]
     * Richiesta per ottenere le statistiche di gioco dell'utente (partite vinte, perse, ecc.).
     */
    STATS_REQUEST,

    /**
     * [Server -> Client]
     * Risposta contenente i dati e le statistiche dell'utente richiesti.
     */
    STATS_RESPONSE,

    /**
     * [Client -> Server]
     * Richiesta di disconnessione e invalidazione della sessione corrente.
     */
    LOGOUT_REQUEST,


    // ==========================================
    //             MESSAGGI DI PARTITA
    // ==========================================


    /**
     * [Server -> Client]
     * Invia al client una nuova domanda della partita con le relative opzioni.
     */
    GAME_QUESTION,

    /**
     * [Client -> Server]
     * Invia al server la risposta selezionata o digitata dall'utente.
     */
    GAME_ANSWER_SUBMIT,

    /**
     * [Server -> Client]
     * Comunica l'esito della risposta appena data (es. corretta/sbagliata, punti ottenuti).
     */
    GAME_ANSWER_RESULT,

    /**
     * [Server -> Client]
     * Segnala che il tempo a disposizione per rispondere alla domanda attuale è scaduto.
     */
    GAME_TIMEOUT,

    /**
     * [Server -> Client]
     * Indica la fine della partita. Il payload contiene il riepilogo (vincitore, punti finali, ecc.).
     */
    GAME_END,

    /**
     * [Server -> Client]
     * Segnala un errore specifico avvenuto durante la partita (es. disconnessione dell'avversario).
     */
    GAME_ERROR,


    // ==========================================
    //                 ERRORI
    // ==========================================

    /**
     * [Server -> Client / Client -> Server]
     * Errore generico o di sistema non classificabile nelle categorie precedenti.
     */
    GENERAL_ERROR
}