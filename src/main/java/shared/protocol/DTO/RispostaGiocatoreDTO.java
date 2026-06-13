/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.protocol.DTO;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) inviato dal Client al Server.
 * Contiene il tentativo logico (parola in chiaro) effettuato dal giocatore
 * nel tentativo di vincere il round corrente.
 */
public class RispostaGiocatoreDTO implements Serializable{
    
    private String parolaTentata;
    
    /**
     * @param parolaTentata La decodifica supposta dal giocatore.
     */
    public RispostaGiocatoreDTO(String parolaTentata) {
        this.parolaTentata = parolaTentata;
    }

    public String getParolaTentata() {
        return parolaTentata;
    }

    public void setParolaTentata(String parolaTentata) {
        this.parolaTentata = parolaTentata;
    }

}
