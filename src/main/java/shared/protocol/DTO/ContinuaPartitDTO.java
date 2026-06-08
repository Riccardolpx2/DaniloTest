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
public class ContinuaPartitDTO implements Serializable{
    
    private boolean vuoleContinuare;

    public ContinuaPartitDTO(boolean vuoleContinuare) {
        this.vuoleContinuare = vuoleContinuare;
    }

    public boolean isVuoleContinuare() {
        return vuoleContinuare;
    }

    public void setVuoleContinuare(boolean vuoleContinuare) {
        this.vuoleContinuare = vuoleContinuare;
    }
    
    
}
