/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.game;

import server.model.database.entity.UtenteEntity;

/**
 *
 * @author Utente
 * 
 */
public class Statistica {
    private UtenteEntity player;
    private int vittorie;
    private int sconfitte;
    private int percentualeVittorie;
    private double mediaRisposta;

    public Statistica(UtenteEntity player, int vittorie, int sconfitte, int percentualeVittorie, double mediaRisposta) {
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
