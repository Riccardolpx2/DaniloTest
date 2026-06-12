/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Utente
 */
public class AnalisiTesto implements Serializable{
    private int idDocumento;
    private Map<String, Integer> frequenzaParole; 
    private static final Set<String> stopWords = caricaStopWords();

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

    private static Set<String> caricaStopWords() {
        try {
            InputStream is = AnalisiTesto.class.getResourceAsStream("/txt/stop-words.txt");
            
            if (is == null) {
                System.out.println("ERRORE: File stop_words_it.txt non trovato in resources!");
                return null; 
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                Set<String> risultato = new java.util.HashSet<>();
                String linea;
            
                while ((linea = reader.readLine()) != null) {
                    linea = linea.trim();
                    linea = linea.toLowerCase();
                
                if (!linea.isEmpty() && !linea.startsWith("#")) {
                    risultato.add(linea);
                }
            }
            return risultato;
            }
        } catch (IOException e) {
            System.out.println("Errore durante il caricamento delle stop words: " + e.getMessage());
            return null;
        }
    }

    
    public void analizza(String testo) {
        if (testo == null || testo.trim().isEmpty()) return;

        String testoPulito = testo.toLowerCase().replaceAll("[^a-zàèìòùáéíóú]", " ");
        this.frequenzaParole = Stream.of(testoPulito.split("\\s+"))
            .filter(parola -> !parola.isEmpty())  
            .filter(parola -> parola.length() > 3)
            .filter(parola -> !stopWords.contains(parola)) 
            .collect(Collectors.toMap( 
                parola -> parola,
                parola -> 1,
                Integer::sum 
            ));
    }
    

    
}
