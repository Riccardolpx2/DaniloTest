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
        if (testo == null || testo.trim().isEmpty()) return;

        String testoPulito = testo.toLowerCase().replaceAll("[^a-zàèìòùáéíóú]", " ");

        this.frequenzaParole = Stream.of(testoPulito.split("\\s+"))
            .filter(parola -> !parola.isEmpty() && parola.length() >= 3)  
            .collect(Collectors.toMap( 
                parola -> parola,
                parola -> 1,
                Integer::sum 
            ));
    }
    

    
}
