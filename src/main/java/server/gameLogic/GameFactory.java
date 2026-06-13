/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameLogic;

import server.gameUtil.Domanda;
import java.sql.SQLException;
import java.util.List;
import server.model.database.DocumentoDAO;
import server.model.database.entity.UtenteEntity;
import server.gameUtil.Documento;
import server.model.database.DomandaDAO;

/**
 * Classe responsabile dell'inizializzazione e della preparazione delle partite (Match).
 * Implementa il pattern Factory per centralizzare la logica di matchmaking, estraendo
 * in modo casuale i documenti di gioco e il relativo set di domande precalcolate dal database 
 * in base ai parametri di configurazione richiesti.
 * * @author Utente
 */
public class GameFactory {
    private final DocumentoDAO documentoDAO = new DocumentoDAO();
    private final DomandaDAO domandaDAO = new DomandaDAO();
    
    
    /**
     * Crea e configura una nuova istanza di un match tra due giocatori.
     * Il metodo seleziona un documento casuale dal database, recupera un set di domande 
     * filtrate per ID documento, livello di difficoltà e quantità, ed esegue i dovuti 
     * controlli di consistenza sui dati prima di generare il gestore della partita.
     * @param p1         Il profilo del primo giocatore (Player 1).
     * @param p2         Il profilo del secondo giocatore (Player 2).
     * @param difficolta Il livello di difficoltà della partita (es. "FACILE", "MEDIO", "DIFFICILE").
     * @param numDomande Il numero di domande previsto per la sessione di gioco.
     * @return Un'istanza di {@link MatchManager} pronta per coordinare il ciclo di vita del match.
     * @throws SQLException Se non sono presenti documenti nel sistema o se il numero di domande precalcolate 
     * nel database per quel testo e difficoltà è inferiore a quello richiesto.
     */
    public MatchManager creaMatch(UtenteEntity p1, UtenteEntity p2, String difficolta, int numDomande) throws SQLException {
        //  Estrae un documento casuale 
        Documento doc = documentoDAO.estraiDocumentoCasuale();
        
        if (doc == null) {
            throw new SQLException("Impossibile avviare il match: non ci sono documenti caricati nel database.");
        }

        // Pesca le domande già pronte e cifrate direttamente dal database
        List<Domanda> domande = domandaDAO.estraiDomandeCasuali(doc.getIdDocumento(), difficolta, numDomande);
        
        // Controllo di sicurezza: verifichiamo se il DB ha abbastanza domande pronte
        if (domande == null || domande.size() < numDomande) {
            System.out.println("ERRORE: Il documento ID " + doc.getIdDocumento() + " non ha abbastanza domande precalcolate nel DB per la difficoltà " + difficolta);
            throw new SQLException("Errore di matchmaking: domande insufficienti nel database per questo testo.");
        }

        System.out.println("Match creato con successo! Caricate " + domande.size() + " domande istantaneamente.");
        
        // Crea e restituisce il matchmanager pronto per giocare
        return new MatchManager(p1, p2, difficolta, domande);
    }
}
