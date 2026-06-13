/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameLogic;

import server.gameUtil.Domanda;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import server.model.database.AnalisiTestoDAO;
import server.model.database.DocumentoDAO;
import server.model.database.entity.UtenteEntity;
import server.gameUtil.AnalisiTesto;
import server.gameUtil.Documento;
import server.model.database.DomandaDAO;

/**
 *
 * @author Utente
 */
public class GameFactory {
    private final DocumentoDAO documentoDAO = new DocumentoDAO();
    private final DomandaDAO domandaDAO = new DomandaDAO();

    public MatchManager creaMatch(UtenteEntity p1, UtenteEntity p2, String difficolta, int numDomande) throws SQLException {
        // 1. Estrae un documento casuale 
        Documento doc = documentoDAO.estraiDocumentoCasuale();
        
        if (doc == null) {
            throw new SQLException("Impossibile avviare il match: non ci sono documenti caricati nel database.");
        }

        // 2. Pesca le domande già pronte e cifrate direttamente dal database
        List<Domanda> domande = domandaDAO.estraiDomandeCasuali(doc.getIdDocumento(), difficolta, numDomande);
        
        // 3. Controllo di sicurezza: verifichiamo se il DB ha abbastanza domande pronte
        if (domande == null || domande.size() < numDomande) {
            System.out.println("ERRORE: Il documento ID " + doc.getIdDocumento() + " non ha abbastanza domande precalcolate nel DB per la difficoltà " + difficolta);
            throw new SQLException("Errore di matchmaking: domande insufficienti nel database per questo testo.");
        }

        System.out.println("Match creato con successo! Caricate " + domande.size() + " domande istantaneamente.");
        
        // 4. Crea e restituisce il matchmanager pronto per giocare
        return new MatchManager(p1, p2, difficolta, domande);
    }
}
