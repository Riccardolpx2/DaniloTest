/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.protocol.DTO;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) inviato dal Client al Server per 
 * manifestare la volontà di mettersi in coda di attesa (matchmaking).
 */
public class GameSearchDTO implements Serializable{
    
    private String difficoltaPartita;

    /**
     * Inizializza la richiesta di ricerca.
     * 
     * @param difficoltaPartita La difficoltà selezionata dall'utente (es. "Facile", "Difficile").
     */
    public GameSearchDTO(String difficoltaPartita) {
        this.difficoltaPartita = difficoltaPartita;
    }

    public String getDifficoltaPartita() {
        return difficoltaPartita;
    }

    public void setDifficoltaPartita(String difficoltaScelta) {
        this.difficoltaPartita = difficoltaPartita;
    }
    
    
    
}
