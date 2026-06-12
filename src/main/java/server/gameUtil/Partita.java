/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import server.model.database.entity.UtenteEntity;

/**
 *
a * @author Utente
 */
public class Partita {
    private int idPartita;
    private LocalDateTime dataInizio;
    private int durataPartita;
    private String stato;
    
    private UtenteEntity player1;
    private UtenteEntity player2;
    private UtenteEntity vincitore;
    
    private int punteggioTotaleG1;
    private int punteggioTotaleG2;
    
    private  List<Integer> tempiRispostaG1;
    private  List<Integer> tempiRispostaG2;

    // COSTRUTTORE 1: Completo (utile al DAO per ricostruire l'oggetto quando fa la "cerca" sul DB)
    public Partita(int idPartita, LocalDateTime dataInizio, int durataPartita, String stato, UtenteEntity player1,
            UtenteEntity player2, UtenteEntity vincitore, int punteggioTotaleG1, int punteggioTotaleG2) {
        this.idPartita = idPartita;
        this.dataInizio = dataInizio;
        this.durataPartita = durataPartita;
        this.stato = stato;
        this.player1 = player1;
        this.player2 = player2;
        this.vincitore = vincitore;
        this.punteggioTotaleG1 = punteggioTotaleG1;
        this.punteggioTotaleG2 = punteggioTotaleG2;
        this.tempiRispostaG1 = new ArrayList<>();
        this.tempiRispostaG2 = new ArrayList<>();
    }

    // COSTRUTTORE 2: Snello (fondamentale per il MatchManager a inizio partita)
    public Partita(UtenteEntity player1, UtenteEntity player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.dataInizio = LocalDateTime.now();
        this.stato = "IN_CORSO";
        this.durataPartita = 0;
        this.punteggioTotaleG1 = 0;
        this.punteggioTotaleG2 = 0;
        this.vincitore = null;
        this.tempiRispostaG1 = new ArrayList<>();
        this.tempiRispostaG2 = new ArrayList<>();
    }

    // --- METODI DI LOGICA AGGIUNTI ---

    /**
     * Registra i tempi di risposta accumulati nel round corrente.
     */
    public void registraTempiRound(int tempoG1, int tempoG2) {
        this.tempiRispostaG1.add(tempoG1);
        this.tempiRispostaG2.add(tempoG2);
    }
    
    public List<Integer> getTempiRispostaG1() {
        return tempiRispostaG1;
    }

    public List<Integer> getTempiRispostaG2() {
        return tempiRispostaG2;
    }

    // --- GETTER E SETTER STANDARD (I tuoi originali) ---
    
    public int getIdPartita() {
        return idPartita;
    }

    public void setIdPartita(int idPartita) {
        this.idPartita = idPartita;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    public int getDurataPartita() {
        return durataPartita;
    }

    public void setDurataPartita(int durataPartita) {
        this.durataPartita = durataPartita;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public UtenteEntity getPlayer1() {
        return player1;
    }

    public void setPlayer1(UtenteEntity player1) {
        this.player1 = player1;
    }

    public UtenteEntity getPlayer2() {
        return player2;
    }

    public void setPlayer2(UtenteEntity player2) {
        this.player2 = player2;
    }

    public UtenteEntity getVincitore() {
        return vincitore;
    }

    public void setVincitore(UtenteEntity vincitore) {
        this.vincitore = vincitore;
    }

    public int getPunteggioTotaleG1() {
        return punteggioTotaleG1;
    }

    public void setPunteggioTotaleG1(int punteggioTotaleG1) {
        this.punteggioTotaleG1 = punteggioTotaleG1;
    }

    public int getPunteggioTotaleG2() {
        return punteggioTotaleG2;
    }

    public void setPunteggioTotaleG2(int punteggioTotaleG2) {
        this.punteggioTotaleG2 = punteggioTotaleG2;
    }

    public void setTempiRispostaG1(List<Integer> tempiRispostaG1) {
        this.tempiRispostaG1 = tempiRispostaG1;
    }

    public void setTempiRispostaG2(List<Integer> tempiRispostaG2) {
        this.tempiRispostaG2 = tempiRispostaG2;
    }
    
    
}