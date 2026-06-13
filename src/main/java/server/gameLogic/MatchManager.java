package server.gameLogic;

import server.gameUtil.Domanda;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.gameUtil.Partita;

import server.model.database.entity.UtenteEntity;
import shared.protocol.DTO.EsitoPartitaDTO;
import shared.protocol.DTO.EsitoRoundDTO;
import shared.protocol.DTO.RispostaGiocatoreDTO;

/**
 * Gestisce la logica di dominio di una partita in corso.
 * Mantiene lo stato dei giocatori, il punteggio, le domande e gestisce l'evoluzione dei round.
 */
public class MatchManager {
    // Servizio di persistenza per salvare la partita e aggiornare le statistiche globali
    private final GameService gameService = new GameService();

    // Dati generali del match in corso
    private final Partita partitaInCorso;
    private final List<Domanda> domande;
    private final String difficolta;
    
    // Stato di avanzamento della partita
    private int indiceRoundCorrente;
    private Domanda domandaCorrente;

    // Riferimenti ai due giocatori
    private final UtenteEntity p1;
    private final UtenteEntity p2;

    // STATO DEL MATCH (Punteggi)
    private int punteggioG1;
    private int punteggioG2;

    // STATO DEL ROUND CORRENTE (Dati temporanei che si resettano a ogni round)
    private String ultimaRispostaG1;
    private String ultimaRispostaG2;
    private int tempoG1;
    private int tempoG2;
    private boolean haIndovinatoG1;
    private boolean haIndovinatoG2;

    /**
     * Costruttore: inizializza i giocatori, la difficoltà della partita e la lista delle domande.
     * @param p1 Il primo utente partecipante.
     * @param p2 Il secondo utente partecipante.
     * @param difficolta La difficoltà scelta per la partita ("FACILE", "MEDIA", "DIFFICILE").
     * @param domande La lista di domande caricate per questa partita.
     */
    public MatchManager(UtenteEntity p1, UtenteEntity p2, String difficolta, List<Domanda> domande) {
        // TODO: Implementare l'inizializzazione delle variabili interne
        this.p1 = p1;
        this.p2 = p2;
        this.difficolta = difficolta;
        this.domande = domande;
        this.indiceRoundCorrente = -1; // La partita non è ancora iniziata (-1)
        
        // Inizializzazione punteggi globali del match
        this.punteggioG1 = 0;
        this.punteggioG2 = 0;

        // Creazione dell'oggetto Partita per lo storico a DB (Costruttore Snello)
        this.partitaInCorso = new Partita(p1, p2);
    }

    /**
     * Prepara e restituisce la nuova domanda per il round successivo.
     * Si occupa anche di resettare le variabili temporanee del round precedente
     * (es. imposta le risposte vuote e i timer a 30s di default per gestire eventuali timeout).
     * * @return La nuova Domanda da sottoporre ai giocatori, oppure null se le domande sono finite.
     */
    public Domanda iniziaNuovoRound() {
    indiceRoundCorrente++;
        
        // Se abbiamo esaurito le domande, la partita è finita naturalmente
        if (indiceRoundCorrente >= domande.size()) {
            this.domandaCorrente = null;
            return null;
        }

        this.domandaCorrente = domande.get(indiceRoundCorrente);

        // Reset dello stato del round per ciascun giocatore (Default a 30s per i timeout)
        this.ultimaRispostaG1 = "";
        this.ultimaRispostaG2 = "";
        this.tempoG1 = 30;
        this.tempoG2 = 30;
        this.haIndovinatoG1 = false;
        this.haIndovinatoG2 = false;

        return this.domandaCorrente;
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
        if (domandaCorrente == null) {
            return false;
        }

        // La parola da indovinare inserita dal GeneratoreDomanda è sempre a indice 0
        String soluzioneUfficiale = domandaCorrente.getParoleSoluzioni().get(0);
        boolean esatta = soluzioneUfficiale.equalsIgnoreCase(risposta.getParolaTentata().trim());

        // Verifichiamo quale dei due giocatori sta rispondendo in base allo username
        if (utente.getUsername().equals(p1.getUsername())) {
            this.ultimaRispostaG1 = risposta.getParolaTentata();
            this.tempoG1 = tempo;
            this.haIndovinatoG1 = esatta;
        } else if (utente.getUsername().equals(p2.getUsername())) {
            this.ultimaRispostaG2 = risposta.getParolaTentata();
            this.tempoG2 = tempo;
            this.haIndovinatoG2 = esatta;
        } else {
            return false; // L'utente non fa parte di questa partita
        }

        return esatta;
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
        if (domandaCorrente == null) return null;

        // Registriamo i tempi di questo round nell'oggetto Partita per il DB
        partitaInCorso.registraTempiRound(this.tempoG1, this.tempoG2);

        String usernameVincitoreRound = null;

        // VALUTAZIONE DEL VINCITORE DEL ROUND
        if (haIndovinatoG1 && haIndovinatoG2) {
            if (tempoG1 < tempoG2) {
                usernameVincitoreRound = p1.getUsername();
                punteggioG1++;
            } else if (tempoG2 < tempoG1) {
                usernameVincitoreRound = p2.getUsername();
                punteggioG2++;
            } else {
                // Pareggio perfetto nel tempo di risposta: un punto a testa
                punteggioG1++;
                punteggioG2++;
            }
        } else if (haIndovinatoG1) {
            usernameVincitoreRound = p1.getUsername();
            punteggioG1++;
        } else if (haIndovinatoG2) {
            usernameVincitoreRound = p2.getUsername();
            punteggioG2++;
        }

        // Generiamo la mappa dei punteggi richiesti dal costruttore di EsitoRoundDTO
        Map<String, Integer> punteggiAttuali = new HashMap<>();
        punteggiAttuali.put(p1.getUsername(), punteggioG1);
        punteggiAttuali.put(p2.getUsername(), punteggioG2);

        String soluzioneUfficiale = domandaCorrente.getParoleSoluzioni().get(0);

        return new EsitoRoundDTO(usernameVincitoreRound, soluzioneUfficiale, punteggiAttuali);
    }

    /**
     * Gestisce la fine definita della partita, sia essa naturale o per abbandono.
     * * @param quitter L'utente che ha abbandonato la partita. Se viene passato 'null', significa che
     * la partita è giunta al termine naturalmente e il vincitore va calcolato in base ai punti.
     * @throws SQLException In caso di errori nell'aggiornamento delle statistiche sul Database.
     * @return L'esito della partita.
     */
public EsitoPartitaDTO terminaPartita(UtenteEntity quitter) throws SQLException {
        UtenteEntity vincitoreMatch = null;
        boolean perAbbandono = (quitter != null);

        if (!perAbbandono) {
            // FINE NATURALE: Vince chi ha totalizzato più punti nei round
            if (punteggioG1 > punteggioG2) {
                vincitoreMatch = p1;
            } else if (punteggioG2 > punteggioG1) {
                vincitoreMatch = p2;
            }
        } else {
            // FINE PER ABBANDONO: Vince a tavolino chi NON ha abbandonato
            if (quitter.getUsername().equals(p1.getUsername())) {
                vincitoreMatch = p2;
            } else {
                vincitoreMatch = p1;
            }
        }

        // Impostiamo i dati finali accumulati nell'oggetto Partita prima di passarlo al DB
        partitaInCorso.setVincitore(vincitoreMatch);
        partitaInCorso.setPunteggioTotaleG1(punteggioG1);
        partitaInCorso.setPunteggioTotaleG2(punteggioG2);
        partitaInCorso.setStato(perAbbandono ? "TERMINATA_ABBANDONO" : "TERMINATA");

        // SALVATAGGIO REALE SUL DB (Salva il match e aggiorna le statistiche globali)
        gameService.terminaESalvaPartita(partitaInCorso);

        // Prepariamo la mappa dei punteggi finali richiesta dal tuo EsitoPartitaDTO
        Map<String, Integer> punteggiFinali = new HashMap<>();
        punteggiFinali.put(p1.getUsername(), punteggioG1);
        punteggiFinali.put(p2.getUsername(), punteggioG2);

        String usernameVincitore = (vincitoreMatch != null) ? vincitoreMatch.getUsername() : null;

        return new EsitoPartitaDTO(usernameVincitore, punteggiFinali, perAbbandono);
    }

    /**
     * Recupera il punteggio attuale totalizzato da uno specifico utente durante questa partita.
     * * @param utente L'entità utente di cui si vuole conoscere il punteggio.
     * @return Il punteggio attuale dell'utente. Restituisce 0 se l'utente non fa parte della partita.
     */
    public int punteggioAttualeDi(UtenteEntity utente) {
        // TODO: Restituire i punti attuali associati all'utente passato come parametro
if (utente.getUsername().equals(p1.getUsername())) {
            return punteggioG1;
        } else if (utente.getUsername().equals(p2.getUsername())) {
            return punteggioG2;
        }
        return 0; // L'utente non appartiene al match
    }
}
