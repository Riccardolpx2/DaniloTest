/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta l'entità e lo stato di una partita nel sistema.
 * Viene utilizzata sia per tracciare i dati in tempo reale durante il gioco,
 * sia come Oggetto di Dominio per il salvataggio e recupero dal Database (DAO).
 *  author Utente
 */
public class PartitaEntity {
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

    /**
     * **COSTRUTTORE 1: Completo**
     * Utilizzato principalmente dal DAO per ricostruire lo stato storico di una partita
     * precedentemente salvata sul Database.
     * * @param idPartita L'identificativo univoco della partita nel DB.
     * @param dataInizio La dadta di quando è iniziata la partita.
     * @param durataPartita La durata complessiva del match in secondi.
     * @param stato Lo stato finale o corrente della partita.
     * @param player1 Il primo utente partecipante.
     * @param player2 Il secondo utente partecipante.
     * @param vincitore L'utente che ha vinto la partita (null in caso di pareggio o match incompleto).
     * @param punteggioTotaleG1 Punteggio finale accumulato dal Giocatore 1.
     * @param punteggioTotaleG2 Punteggio finale accumulato dal Giocatore 2.
     */
    public PartitaEntity(int idPartita, LocalDateTime dataInizio, int durataPartita, String stato, UtenteEntity player1,
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
    /**
     * **COSTRUTTORE 2: Snello**
     * Utilizzato a runtime dal server/MatchManager per inizializzare una nuova partita
     * non appena due giocatori si accoppiano nel matchmaking.
     * Imposta i punteggi a 0, lo stato a "IN_CORSO" e registra l'orario attuale.
     * @param player1 Il primo utente (es. il creatore della stanza).
     * @param player2 Il secondo utente (es. chi si è aggiunto).
     */
    public PartitaEntity(UtenteEntity player1, UtenteEntity player2) {
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

    /**
     * Registra i tempi di risposta (in secondi) accumulati da entrambi i giocatori
     * nel round corrente, inserendoli nelle rispettive liste storiche.
     * * @param tempoG1 Secondi impiegati dal Giocatore 1.
     * @param tempoG2 Secondi impiegati dal Giocatore 2.
     */
    public void registraTempiRound(int tempoG1, int tempoG2) {
        this.tempiRispostaG1.add(tempoG1);
        this.tempiRispostaG2.add(tempoG2);
    }
    
    /**
     * Restituisce la lista di tutti i tempi di risposta del Giocatore 1 round per round.
     * @return List di Integer contenente i secondi di risposta.
     */
    public List<Integer> getTempiRispostaG1() {
        return tempiRispostaG1;
    }
    
    /**
     * Restituisce la lista di tutti i tempi di risposta del Giocatore 2 round per round.
     * @return List di Integer contenente i secondi di risposta.
     */
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
        if(durataPartita >= 0) this.durataPartita = durataPartita;
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
        if(punteggioTotaleG1 >= 0)
        this.punteggioTotaleG1 = punteggioTotaleG1;
    }

    public int getPunteggioTotaleG2() {
        return punteggioTotaleG2;
    }

    public void setPunteggioTotaleG2(int punteggioTotaleG2) {
        if(punteggioTotaleG2 >= 0)
        this.punteggioTotaleG2 = punteggioTotaleG2;
    }

    public void setTempiRispostaG1(List<Integer> tempiRispostaG1) {
        this.tempiRispostaG1 = tempiRispostaG1;
    }

    public void setTempiRispostaG2(List<Integer> tempiRispostaG2) {
        this.tempiRispostaG2 = tempiRispostaG2;
    }
    
    
}