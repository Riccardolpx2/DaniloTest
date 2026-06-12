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

        //documento random (QUI è giusto farlo)
        List<Documento> docs = documentoDAO.elencaTutti();
        Documento doc = docs.get(new Random().nextInt(docs.size()));

        //analisi collegata al documento
        AnalisiTesto analisi = analisiDAO.elencaTutti().stream().filter(a -> a.getIdDocumento() == doc.getIdDocumento())
                .findFirst().orElse(null);

        if (analisi == null) {
            analisi = new AnalisiTesto(doc.getIdDocumento());
            analisi.analizza(doc.getTesto());
        }

        //genera domande
        List<Domanda> domande = generatoreDomanda.creaDomande(numDomande, difficolta, doc, analisi);

        //crea match
        return new MatchManager(p1, p2, difficolta, domande);
    }    
    
}
