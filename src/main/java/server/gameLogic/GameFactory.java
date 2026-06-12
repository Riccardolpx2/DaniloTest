/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameLogic;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import server.model.database.AnalisiTestoDAO;
import server.model.database.DocumentoDAO;
import server.model.database.entity.UtenteEntity;
import server.gameUtil.AnalisiTesto;
import server.gameUtil.Documento;

/**
 *
 * @author Utente
 */
public class GameFactory {
    private final DocumentoDAO documentoDAO = new DocumentoDAO();
    private final AnalisiTestoDAO analisiDAO = new AnalisiTestoDAO();
    private final GeneratoreDomanda generatoreDomanda = new GeneratoreDomanda();

    public MatchManager creaMatch(UtenteEntity p1, UtenteEntity p2, String difficolta, int numDomande) throws SQLException {

    Documento doc = documentoDAO.estraiDocumentoCasuale();
        
        if (doc == null) {
            throw new SQLException("Impossibile avviare il match: non ci sono documenti caricati nel database.");
        }

    AnalisiTesto analisi = analisiDAO.cerca(String.valueOf(doc.getIdDocumento()));  
    
    
    if (analisi == null) {
            System.out.println("Analisi assente per il documento " + doc.getIdDocumento() + ". Generazione in corso...");
            analisi = new AnalisiTesto(doc.getIdDocumento());
            analisi.analizza(doc.getTesto());
            analisiDAO.aggiungi(analisi); // Persistenza immediata sul DB relazionale
        }

        //genera domande
        List<Domanda> domande = generatoreDomanda.creaDomande(numDomande, difficolta, doc, analisi);
        
        if (domande == null || domande.isEmpty()) {
         System.out.println("ERRORE: Il documento " + doc.getIdDocumento() + " non ha abbastanza parole per la difficoltà " + difficolta);
         throw new SQLException("Generazione domande fallita per scarsità di lemmi nel testo.");
        }
        //crea match
        return new MatchManager(p1, p2, difficolta, domande);
    }    
    
}
