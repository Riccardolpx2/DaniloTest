/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.logica;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

/**
 *
 * @author Utente
 */
public class MatchManager {

    private SessioneDiGioco sessione;
    private UtenteEntity giocatore1;
    private UtenteEntity giocatore2;
    private Documento documentoPartita;
    private boolean continua = true;

    private PartitaDAO partitaDAO = new PartitaDAO();
    private SessioneDiGiocoDAO sessioneDAO = new SessioneDiGiocoDAO();
    private StatisticaDAO statisticaDAO = new StatisticaDAO();

    private int puntiG1;
    private int puntiG2;

    private Map<String, Integer> mappaFrequenzaDocumento;
    private String difficolta;

    private Random random = new Random();
    private String parolaSoluzioneCorrente; 
    private int shiftCorrente;

    private int inizioFrammentoCorrente;
    private int lunghezzaFrammentoCorrente;

    public MatchManager(UtenteEntity u1, UtenteEntity u2, Documento documentoPartita, String difficolta, AnalisiTesto analisi) {
        this.giocatore1 = u1;
        this.giocatore2 = u2;
        this.documentoPartita = documentoPartita;
        this.difficolta=difficolta;
        this.puntiG1 = 0;
        this.puntiG2 = 0;
        
        this.mappaFrequenzaDocumento = analisi.getFrequenzaParole();
        
        this.sessione = new SessioneDiGioco(
            0, 0, null, LocalDateTime.now(), 
            0, 0, null, u1, u2, "IN_CORSO"
        );
    }
    
    public TestoDTO inizializzaNuovoRound() {  
        int maxFrequenza = mappaFrequenzaDocumento.values().stream()
        .mapToInt(Integer::intValue).max().orElse(1);

        List<String> paroleFiltrate = mappaFrequenzaDocumento.entrySet().stream()
        .filter(entry -> entry.getKey().length() >= 3 
                && rispettaSoglieUtente(entry.getValue(), maxFrequenza, difficolta))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());


        if (paroleFiltrate.isEmpty()) {
        paroleFiltrate = new ArrayList<>(mappaFrequenzaDocumento.keySet());
        }
        
        this.parolaSoluzioneCorrente = paroleFiltrate.get(random.nextInt(paroleFiltrate.size()));
        this.shiftCorrente = random.nextInt(25) + 1;
        
        String testoCompleto = this.documentoPartita.getTesto();
        int indiceParola = testoCompleto.toLowerCase().indexOf(this.parolaSoluzioneCorrente.toLowerCase());
        
        
        int inizioFrammento = Math.max(0, indiceParola - 60);
        int fineFrammento = Math.min(testoCompleto.length(), indiceParola + this.parolaSoluzioneCorrente.length() + 60);
        
        this.inizioFrammentoCorrente = inizioFrammento;
        this.lunghezzaFrammentoCorrente = fineFrammento - inizioFrammento;
        
        String testoPrecedente = testoCompleto.substring(inizioFrammento, indiceParola);
        String parolaOriginaleNelTesto = testoCompleto.substring(indiceParola, indiceParola + this.parolaSoluzioneCorrente.length());
        String testoSuccessivo = testoCompleto.substring(indiceParola + this.parolaSoluzioneCorrente.length(), fineFrammento);
        
        String parolaCifrata = cifraCesare(parolaOriginaleNelTesto, this.shiftCorrente);
        List<String> paroleOscurate = new ArrayList<>();
        paroleOscurate.add(parolaCifrata);
        
        
        return new TestoDTO(testoPrecedente, paroleOscurate, testoSuccessivo);
    }
    
    private boolean rispettaSoglieUtente(int frequenza, int maxFrequenza, String difficolta) {
        double percentuale = (double) frequenza / maxFrequenza * 100;
        boolean risultato = false;
        switch (difficolta.toUpperCase()) {
            case "FACILE":
                risultato = (percentuale >= 50.0);
                break;
            case "MEDIA":
                risultato = (percentuale >= 10.0 && percentuale <= 40.0);
                break;
            case "DIFFICILE":
                risultato = (percentuale < 10.0);
                break;
            default:
                risultato = false;
                break;
        }
        
        return risultato;
        }
    
    private String cifraCesare(String parola, int shift) {
        StringBuilder risultato = new StringBuilder();

        for (char carattere : parola.toCharArray()) {
            if (Character.isLetter(carattere)) {
                char base = Character.isUpperCase(carattere) ? 'A' : 'a';
            
                char carattereCifrato = (char) ((carattere - base + shift) % 26 + base);
                risultato.append(carattereCifrato);
            } else {
                risultato.append(carattere); 
            }   
        }
    
        return risultato.toString();
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
    
    public EsitoRoundDTO registraEsitoRound(RispostaGiocatoreDTO rispG1, RispostaGiocatoreDTO rispG2) throws SQLException {

        UtenteEntity vincitoreRound = determinaVincitoreRound(this.parolaSoluzioneCorrente,rispG1.getParolaTentata(),
                rispG1.getTempo(),rispG2.getParolaTentata(), rispG2.getTempo());

        if (vincitoreRound != null) {
            if (vincitoreRound.getUsername().equals(this.giocatore1.getUsername())) {
                this.puntiG1++;
                this.sessione.incrementaPunteggioG1(1);
            } else {
                this.puntiG2++;
                this.sessione.incrementaPunteggioG2(1);
            }
        }
        
        Partita round = new Partita( 0, this.sessione.getIdSessione(), this.inizioFrammentoCorrente, this.lunghezzaFrammentoCorrente, shiftCorrente,
                this.parolaSoluzioneCorrente,rispG1.getTempo(), rispG2.getTempo(), difficolta, vincitoreRound, this.documentoPartita);

        partitaDAO.aggiungi(round);
        this.sessione.aggiungiPartita(round);
        
        String nomeVincitore = (vincitoreRound != null) ? vincitoreRound.getUsername() : "Pareggio";
        return new EsitoRoundDTO(nomeVincitore, this.parolaSoluzioneCorrente, this.puntiG1, this.puntiG2);
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
        boolean isNuovo = false;
        if (stat == null) {
            stat = new Statistica(utente, 0, 0, 0, 0.0);
            isNuovo = true;
        }
        if (haVinto) {
            stat.setVittorie(stat.getVittorie() + 1);
        } else {
            stat.setSconfitte(stat.getSconfitte() + 1);
        }
        int totali = stat.getVittorie() + stat.getSconfitte();
        stat.setPercentualeVittorie((stat.getVittorie() * 100) / totali);

        if (isNuovo) {
            statisticaDAO.aggiungi(stat);
        } else {
            statisticaDAO.aggiorna(stat);
        }
    }
      
    
}