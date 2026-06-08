package shared.protocol;

import java.io.Serializable;


public enum MessageType implements Serializable {
    // -------- Messaggi di Autenticazione --------

    login,
    loginSuccess,
    loginFailure,

    register,
    registerSuccess,
    registerFailure,


    // --------- Messaggi di Dashboard ---------

    // Il client lo invia quando cerca una partita
    gameSearch,
    // Il client lo invia per annullare la ricerca in corso
    gameSearchCancel,
    // Il server lo invia con le informazioni dello sfidante, la modalità di difficoltà ecc.
    gameStart,
    // Il server lo invia per segnalare un errore nella ricerca partita
    gameSearchError,

    // Il client invia una richiesta per vedere le proprie statistiche
    stats,
    // Il server invia le informazioni associate all'utente
    statsInfo,
    logout,


    // --------- Messaggi di Partita ---------

    // Il server invia ai client la domanda con annesso payload
    gameQuestion,
    // Il client invia le risposte alla domanda
    gameAnswer,
    // Il server invia al client il risultato della risposta data, con annesso payload
    gameResponse,
    // Il server invia il timeout al client
    gameTimeOut,
    // Il server invia ai client la fine della partita con il riepilogo (vincitore, punti, ecc.)
    gameEnd,

    gameError,

    // ----------- Errori ----------
    // Errore generale non ancora definito
    generalError,
}
