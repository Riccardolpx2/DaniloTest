/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.game;

import java.time.LocalDateTime;
import java.util.List;
import server.model.database.entity.UtenteEntity;

/**
 *
 * @author Utente
 */
public class SessioneDiGioco {
    private int idSessione;
    private int durataSessione;
    private List<Partita> partite;
    private LocalDateTime dataInizio;
    
    private int punteggioTotaleG1;
    private int punteggioTotaleG2;
    
    private UtenteEntity vincitore;
    private UtenteEntity player1;
    private UtenteEntity player2;
    
    private String stato;

    public SessioneDiGioco(int idSessione, int durataSessione, List<Partita> partite, LocalDateTime dataInizio, int punteggioTotaleG1, int punteggioTotaleG2, UtenteEntity vincitore, UtenteEntity player1, UtenteEntity player2, String stato) {
        this.idSessione = idSessione;
        this.durataSessione = durataSessione;
        this.partite = partite;
        this.dataInizio = dataInizio;
        this.punteggioTotaleG1 = punteggioTotaleG1;
        this.punteggioTotaleG2 = punteggioTotaleG2;
        this.vincitore = vincitore;
        this.player1 = player1;
        this.player2 = player2;
        this.stato=stato;
    }

    public int getIdSessione() {
        return idSessione;
    }

    public void setIdSessione(int idSessione) {
        this.idSessione = idSessione;
    }

    public int getDurataSessione() {
        return durataSessione;
    }

    public void setDurataSessione(int durataSessione) {
        this.durataSessione = durataSessione;
    }

    public List<Partita> getPartite() {
        return partite;
    }

    public void setPartite(List<Partita> partite) {
        this.partite = partite;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
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

    public UtenteEntity getVincitore() {
        return vincitore;
    }

    public void setVincitore(UtenteEntity vincitore) {
        this.vincitore = vincitore;
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

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

 
    
}
