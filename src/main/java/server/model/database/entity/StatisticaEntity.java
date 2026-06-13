/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.model.database.entity;

/**
 * Rappresenta il riepilogo delle statistiche di gioco storiche di uno specifico utente.
 * Viene utilizzata per tracciare le performance (vittorie, sconfitte, tempi medi) 
 * @author Utente
 */
public class StatisticaEntity {
    private UtenteEntity player;
    private int vittorie;
    private int sconfitte;
    private int percentualeVittorie;
    private double mediaRisposta;

    /**
     * Costruttore completo per l'inizializzazione delle statistiche di un giocatore.
     * Viene invocato dal DAO quando recupera i dati storici dal Database.
     * @param player L'entità utente a cui si riferiscono queste statistiche.
     * @param vittorie Il numero totale di partite vinte.
     * @param sconfitte Il numero totale di partite perse.
     * @param percentualeVittorie La percentuale di successo (vittorie rispetto al totale dei match).
     * @param mediaRisposta Il tempo medio di risposta del giocatore espresso in secondi.
     */
    public StatisticaEntity(UtenteEntity player, int vittorie, int sconfitte, int percentualeVittorie, double mediaRisposta) {
        this.player = player;
        this.vittorie = vittorie;
        this.sconfitte = sconfitte;
        this.percentualeVittorie = percentualeVittorie;
        this.mediaRisposta = mediaRisposta;
    }
    
    public UtenteEntity getPlayer() {
        return player;
    }

    public void setPlayer(UtenteEntity player) {
        this.player = player;
    }

    public int getVittorie() {
        return vittorie;
    }

    public void setVittorie(int vittorie) {
        this.vittorie = vittorie;
    }

    public int getSconfitte() {
        return sconfitte;
    }

    public void setSconfitte(int sconfitte) {
        this.sconfitte = sconfitte;
    }

    public int getPercentualeVittorie() {
        return percentualeVittorie;
    }

    public void setPercentualeVittorie(int percentualeVittorie) {
        this.percentualeVittorie = percentualeVittorie;
    }

    public double getMediaRisposta() {
        return mediaRisposta;
    }

    public void setMediaRisposta(double mediaRisposta) {
        this.mediaRisposta = mediaRisposta;
    }
    
    
}
