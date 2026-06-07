/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.logica;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import server.model.database.PartitaDAO;
import server.model.database.SessioneDiGiocoDAO;
import server.model.database.StatisticaDAO;
import server.model.database.entity.UtenteEntity;
import shared.game.AnalisiTesto;
import shared.game.Documento;
import shared.game.Partita;
import shared.game.SessioneDiGioco;
import shared.game.Statistica;

/**
 *
 * @author Utente
 */
public class MatchManager {

    private SessioneDiGioco sessione;

    private UtenteEntity giocatore1;
    private UtenteEntity giocatore2;
    
    private boolean continua = true;
    
    private PartitaDAO partitaDAO = new PartitaDAO();
    private SessioneDiGiocoDAO sessioneDAO = new SessioneDiGiocoDAO();
    private StatisticaDAO statisticaDAO = new StatisticaDAO();

    private int puntiG1;
    private int puntiG2;
    
    private Map<String, Integer> mappaFrequenzaDocumento;
    
    private Random random = new Random();
    private String parolaSoluzioneCorrente; 
    private int shiftCorrente;

    public MatchManager(UtenteEntity u1, UtenteEntity u2, AnalisiTesto analisi) {
        this.giocatore1 = u1;
        this.giocatore2 = u2;

        this.puntiG1 = 0;
        this.puntiG2 = 0;
        
        this.mappaFrequenzaDocumento = analisi.getFrequenzaParole();
        
        this.sessione = new SessioneDiGioco(
            0, 0, null, LocalDateTime.now(), 
            0, 0, null, u1, u2, "IN_CORSO"
        );
    }
       
    public void registraRispostaNuovaPartita(boolean rispG1, boolean rispG2) {
        if (!rispG1 || !rispG2) {
            this.continua = false;
        }
    }
     
    public boolean isSessioneFinita() {
        return !this.continua;
    }
    
    
    public UtenteEntity determinaVincitoreRound(String soluzione, String rispostaG1, int tempoG1,String rispostaG2, int tempoG2) {
        boolean g1Corretto = rispostaG1.equalsIgnoreCase(soluzione);
        boolean g2Corretto = rispostaG2.equalsIgnoreCase(soluzione);
        
        if (!g1Corretto && !g2Corretto) return null; 

        if (g1Corretto && !g2Corretto) return this.giocatore1;

        if (!g1Corretto && g2Corretto) return this.giocatore2;

        if (tempoG1 < tempoG2) return this.giocatore1;
        else if (tempoG2 < tempoG1) return this.giocatore2;
        return null;
    }
    
    public void registraEsitoRound(Documento doc, String parolaChiaro, int shift, String difficolta,
                String rispostaG1, int tempoG1, String rispostaG2, int tempoG2) throws SQLException {

        UtenteEntity vincitoreRound = determinaVincitoreRound(parolaChiaro, rispostaG1, tempoG1, rispostaG2, tempoG2);

        if (vincitoreRound != null) {
            if (vincitoreRound.getUsername().equals(this.giocatore1.getUsername())) {
                this.puntiG1++;
                this.sessione.incrementaPunteggioG1(1);
            } else {
                this.puntiG2++;
                this.sessione.incrementaPunteggioG2(1);
            }
        }
        int offsetIniziale = doc.getTesto().indexOf(parolaChiaro);
        if (offsetIniziale == -1) offsetIniziale = 0;
        
        Partita round = new Partita( 0, this.sessione.getIdSessione(), offsetIniziale, parolaChiaro.length(), shift, parolaChiaro, 
            tempoG1, tempoG2, difficolta, vincitoreRound, doc);

        partitaDAO.aggiungi(round);
        this.sessione.aggiungiPartita(round);
    }
    
    public void terminaSessione() throws SQLException {
        this.sessione.setStato("TERMINATA");
        
        UtenteEntity vincitore = null;
        UtenteEntity perdente = null;

        if (this.puntiG1 > this.puntiG2) {
            vincitore= this.giocatore1;
            perdente = this.giocatore2;
        } else if (this.puntiG2 > this.puntiG1) {
            vincitore = this.giocatore2;
            perdente = this.giocatore1;
        } 
        this.sessione.setVincitore(vincitore);
        sessioneDAO.aggiorna(this.sessione);

        if (vincitore!= null && perdente != null) {
            aggiornaStatisticheStoriche(vincitore, true);
            aggiornaStatisticheStoriche(perdente, false);
        }
    }

    private void aggiornaStatisticheStoriche(UtenteEntity utente, boolean haVinto) throws SQLException {
        Statistica stat = statisticaDAO.cerca(utente.getUsername());
        
        if (stat == null) {
            stat = new Statistica(utente, 0, 0, 0, 0.0);
            statisticaDAO.aggiungi(stat);
        }
        if (haVinto) {
            stat.setVittorie(stat.getVittorie() + 1);
        } else {
            stat.setSconfitte(stat.getSconfitte() + 1);
        }
        int totali = stat.getVittorie() + stat.getSconfitte();
        stat.setPercentualeVittorie((stat.getVittorie() * 100) / totali);

        statisticaDAO.aggiorna(stat);
    }
    
}