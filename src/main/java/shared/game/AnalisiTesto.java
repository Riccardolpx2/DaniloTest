/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.game;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Utente
 */
public class AnalisiTesto {
    private int idDocumento;
    private Map<String, Integer> frequenzaParole; 

    public AnalisiTesto(int idDocumento) {
        this.idDocumento= idDocumento;
        this.frequenzaParole = new HashMap<>();
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Map<String, Integer> getFrequenzaParole() {
        return frequenzaParole;
    }

    public void setFrequenzaParole(Map<String, Integer> frequenzaParole) {
        this.frequenzaParole = frequenzaParole;
    }
    

    
}
