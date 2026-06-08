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
public class SceltaDifficoltaDTO implements Serializable{
    
    private String difficoltaScelta; 

    public SceltaDifficoltaDTO( String difficoltaScelta) {
        this.difficoltaScelta = difficoltaScelta;
    }

    public String getDifficoltaScelta() {
        return difficoltaScelta;
    }

    public void setDifficoltaScelta(String difficoltaScelta) {
        this.difficoltaScelta = difficoltaScelta;
    }
    
    
    
}
