package server.gameLogic;

import java.sql.SQLException;
import java.util.List;

import server.model.database.entity.UtenteEntity;
import shared.protocol.DTO.EsitoRoundDTO;
import shared.protocol.DTO.RispostaGiocatoreDTO;

/**
 * Gestisce la logica di dominio di una partita in corso.
 * Mantiene lo stato dei giocatori, il punteggio, le domande e gestisce l'evoluzione dei round.
 */
public class MatchManager {

    /**
     * Costruttore: inizializza i giocatori, la difficoltà della partita e la lista delle domande.
     * @param p1 Il primo utente partecipante.
     * @param p2 Il secondo utente partecipante.
     * @param difficolta La difficoltà scelta per la partita ("FACILE", "MEDIA", "DIFFICILE").
     * @param domande La lista di domande caricate per questa partita.
     */
    public MatchManager(UtenteEntity p1, UtenteEntity p2, String difficolta, List<Domanda> domande) {
        // TODO: Implementare l'inizializzazione delle variabili interne
    }

    /**
     * Prepara e restituisce la nuova domanda per il round successivo.
     * Si occupa anche di resettare le variabili temporanee del round precedente
     * (es. imposta le risposte vuote e i timer a 30s di default per gestire eventuali timeout).
     * * @return La nuova Domanda da sottoporre ai giocatori, oppure null se le domande sono finite.
     */
    public Domanda iniziaNuovoRound() {
        // TODO: Implementare la logica di avanzamento e reset stato round
        return null;
    }

    /**
     * Registra la risposta fornita da un utente durante il round corrente e verifica se è la soluzione.
     * * @param utente L'utente che ha inviato la risposta.
     * @param risposta Il DTO contenente la parola tentata dal giocatore.
     * @param tempo I secondi impiegati dal giocatore per rispondere.
     * @return true se la risposta è corretta (utile al Thread per fermare l'attesa), false altrimenti.
     */
    public boolean elaboraRisposta(UtenteEntity utente, RispostaGiocatoreDTO risposta, int tempo) {
        // TODO: Salvare localmente la risposta associata all'utente
        // TODO: Confrontare la stringa contenuta nel DTO con le soluzioni della domanda corrente
        return false;
    }

    /**
     * Viene invocato alla fine del timer o non appena il round si sblocca (qualcuno ha indovinato o tutti hanno risposto).
     * Valuta le risposte salvate in precedenza, determina il vincitore del singolo round, aggiorna i punti
     * dei giocatori e prepara il risultato da inviare.
     * * @return Il DTO con l'esito del round pronto per essere inviato ai client.
     * @throws SQLException In caso di errori durante eventuali salvataggi a Database.
     */
    public EsitoRoundDTO chiudiRound() throws SQLException {
        // TODO: Calcolare chi ha vinto il round (chi ha indovinato col tempo minore)
        // TODO: Incrementare il punteggio del vincitore
        // TODO: Costruire e restituire l'EsitoRoundDTO
        return null;
    }

    /**
     * Gestisce la fine definita della partita, sia essa naturale o per abbandono.
     * * @param quitter L'utente che ha abbandonato la partita. Se viene passato 'null', significa che
     * la partita è giunta al termine naturalmente e il vincitore va calcolato in base ai punti.
     * @throws SQLException In caso di errori nell'aggiornamento delle statistiche sul Database.
     */
    public void terminaPartita(UtenteEntity quitter) throws SQLException {
        // TODO:
        // 1. Se quitter == null -> Calcola il vincitore guardando chi ha più punti.
        // 2. Se quitter != null -> Assegna la vittoria a tavolino all'utente che NON ha abbandonato.
        // 3. Salva o Aggiorna le Statistiche (es. ratio vittorie/sconfitte) dei due utenti nel DB.
    }

    /**
     * Recupera il punteggio attuale totalizzato da uno specifico utente durante questa partita.
     * * @param utente L'entità utente di cui si vuole conoscere il punteggio.
     * @return Il punteggio attuale dell'utente. Restituisce 0 se l'utente non fa parte della partita.
     */
    public int punteggioAttualeDi(UtenteEntity utente) {
        // TODO: Restituire i punti attuali associati all'utente passato come parametro
        return 0;
    }
}