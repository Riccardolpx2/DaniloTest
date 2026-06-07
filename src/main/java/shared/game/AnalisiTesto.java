/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.game;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Utente
 */
public class AnalisiTesto implements Serializable{
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
    
    public void analizza(String testo) {
        if (testo == null) return;
        this.frequenzaParole = Stream.of(testo.toLowerCase().split("\\s+"))
            .map(parola -> parola.replaceAll("[^a-zàèìòù]", "")) 
            .filter(parola -> parola.length() > 3)  
            .collect(Collectors.toMap( 
                parola -> parola,
                parola -> 1,
                Integer::sum 
            ));
    }
    

    
}
