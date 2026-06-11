/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.protocol.DTO;

import java.io.Serializable;


public class GameSearchDTO implements Serializable{
    
    private String difficoltaPartita;

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
