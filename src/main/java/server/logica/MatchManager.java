package server.logica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import server.model.database.PartitaDAO;
import server.model.database.SessioneDiGiocoDAO;
import server.model.database.StatisticaDAO;
import server.model.database.entity.UtenteEntity;
import shared.game.AnalisiTesto;
import shared.game.Documento;
import shared.game.Partita;
import shared.game.SessioneDiGioco;
import shared.game.Statistica;
import shared.protocol.DTO.EsitoRoundDTO;
import shared.protocol.DTO.RispostaGiocatoreDTO;
import shared.protocol.DTO.TestoDTO;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MatchManager {

    private final UtenteEntity giocatore1;
    private final UtenteEntity giocatore2;

    private final List<Domanda> listaDomande;
    private int indiceRound = 0;

    private Domanda domandaCorrente;
   
    private final SessioneDiGioco sessione;

    private int puntiG1 = 0;
    private int puntiG2 = 0;

    private boolean continua = true;

    private Partita roundCorrente;

    private final String difficolta;

    private final GameService gameService = new GameService();

    public MatchManager(UtenteEntity p1,UtenteEntity p2,String difficolta,List<Domanda> domande) {

        this.giocatore1 = p1;
        this.giocatore2 = p2;
        this.listaDomande = domande;
        this.difficolta = difficolta;

        this.sessione = new SessioneDiGioco( 0, 0, null, LocalDateTime.now(), 0, 0, null,p1, p2,"IN_CORSO");
    }

    public Domanda getDomanda() {

        if (indiceRound >= listaDomande.size()) {
            continua = false;
            return null;
        }
        domandaCorrente = listaDomande.get(indiceRound);
        indiceRound++;

        roundCorrente = new Partita(0,sessione.getIdSessione(),0,0,0,domandaCorrente.getParoleSoluzioni().get(0),0,
                0,difficolta, null,null);

        return domandaCorrente;
    }

    public EsitoRoundDTO registraEsitoRound(RispostaGiocatoreDTO g1,RispostaGiocatoreDTO g2) throws SQLException {

        UtenteEntity vincitore = determinaVincitore(g1.getParolaTentata(), g1.getTempo(),g2.getParolaTentata(), g2.getTempo());
        if (vincitore != null) {
            if (vincitore.equals(giocatore1)) {
                puntiG1++;
                sessione.incrementaPunteggioG1(1);
            } else {
                puntiG2++;
                sessione.incrementaPunteggioG2(1);
            }
        }

        roundCorrente.setSecondiRispostaG1(g1.getTempo());
        roundCorrente.setSecondiRispostaG2(g2.getTempo());
        roundCorrente.setVincitore(vincitore);

        gameService.salvaPartita(roundCorrente);
        sessione.aggiungiPartita(roundCorrente);

        String nomeVincitore =(vincitore != null) ? vincitore.getUsername() : "Pareggio";

        return new EsitoRoundDTO( nomeVincitore, roundCorrente.getParolaSoluzione(),puntiG1,puntiG2 );
    }
    
    private UtenteEntity determinaVincitore(String r1, int t1, String r2, int t2) {

        boolean g1 = domandaCorrente.getParoleSoluzioni().contains(r1.trim().toLowerCase());

        boolean g2 = domandaCorrente.getParoleSoluzioni().contains(r2.trim().toLowerCase());

        if (!g1 && !g2) return null;
        if (g1 && !g2) return giocatore1;
        if (!g1 && g2) return giocatore2;

        return (t1 < t2) ? giocatore1 : (t2 < t1) ? giocatore2 : null;
    }

    public void terminaSessione() throws SQLException {

        sessione.setStato("TERMINATA");
        UtenteEntity vincitore = null;

        if (puntiG1 > puntiG2) vincitore = giocatore1;
        else if (puntiG2 > puntiG1) vincitore = giocatore2;

        sessione.setVincitore(vincitore);
        gameService.salvaSessione(sessione);
        aggiornaStatistiche();
    }

    private void aggiornaStatistiche() throws SQLException {

        aggiornaSingoloUtente(giocatore1, puntiG1 > puntiG2);
        aggiornaSingoloUtente(giocatore2, puntiG2 > puntiG1);
    }

    private void aggiornaSingoloUtente(UtenteEntity u, boolean haVinto) throws SQLException {

        Statistica stat = gameService.getStatistica(u.getUsername());
        if (stat == null) {
            stat = new Statistica(u, 0, 0, 0, 0);
            gameService.creaStatistica(stat);
        }

        if (haVinto) stat.setVittorie(stat.getVittorie() + 1);
        else stat.setSconfitte(stat.getSconfitte() + 1);

        int tot = stat.getVittorie() + stat.getSconfitte();
        stat.setPercentualeVittorie((stat.getVittorie() * 100) / tot);
        gameService.aggiornaStatistica(stat);
    }

    public boolean isFinita() {
        return !continua;
    }

    public SessioneDiGioco getSessione() {
        return sessione;
    }
}