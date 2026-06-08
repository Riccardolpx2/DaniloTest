/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.protocol.DTO;

import java.io.Serializable;

/**
 *
 * @author Utente
 */
public class RispostaGiocatoreDTO implements Serializable{
    
    private String parolaTentata;
    private int tempo;

    public RispostaGiocatoreDTO(String parolaTentata, int tempo) {
        this.parolaTentata = parolaTentata;
        this.tempo = tempo;
    }

    public String getParolaTentata() {
        return parolaTentata;
    }

    public void setParolaTentata(String parolaTentata) {
        this.parolaTentata = parolaTentata;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }
    
    
    
}
