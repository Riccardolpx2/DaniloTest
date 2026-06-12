/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class AnalisiTesto{
private int idDocumento;
    private Map<String, Integer> frequenzaParole; 
    private static final Set<String> stopWords = caricaStopWords();

    public AnalisiTesto(int idDocumento) {
        this.idDocumento = idDocumento;
        this.frequenzaParole = new HashMap<>();
    }

    /**
     * Permette di inserire una coppia parola-frequenza direttamente nella mappa.
     * Utilizzato dall'AnalisiTestoDAO quando ricostruisce l'oggetto dal database.
     */
    public void aggiungiParolaFrequenza(String parola, int frequenza) {
        this.frequenzaParole.put(parola, frequenza);
    }

    // --- GETTER E SETTER (I tuoi originali) ---
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

    // --- LOGICA DI CARICAMENTO FILE (La tua originale) ---
    private static Set<String> caricaStopWords() {
        try {
            InputStream is = AnalisiTesto.class.getResourceAsStream("/txt/stop-words.txt");
            
            if (is == null) {
                System.out.println("ERRORE: File stop-words.txt non trovato in resources!");
                return new java.util.HashSet<>(); // Evita il NullPointerException restituendo un set vuoto
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                Set<String> risultato = new java.util.HashSet<>();
                String linea;
            
                while ((linea = reader.readLine()) != null) {
                    linea = linea.trim().toLowerCase();
                
                    if (!linea.isEmpty() && !linea.startsWith("#")) {
                        risultato.add(linea);
                    }
                }
                return risultato;
            }
        } catch (IOException e) {
            System.out.println("Errore durante il caricamento delle stop words: " + e.getMessage());
            return new java.util.HashSet<>();
        }
    }

    // --- LOGICA DI ANALISI STREAM (La tua originale) ---
    public void analizza(String testo) {
        if (testo == null || testo.trim().isEmpty()) return;

        String testoPulito = testo.toLowerCase().replaceAll("[^a-zàèìòùáéíóú]", " ");
        this.frequenzaParole = Stream.of(testoPulito.split("\\s+"))
            .filter(parola -> !parola.isEmpty())  
            .filter(parola -> parola.length() > 3)
            .filter(parola -> stopWords != null && !stopWords.contains(parola)) 
            .collect(Collectors.toMap( 
                parola -> parola,
                parola -> 1,
                Integer::sum 
            ));
    }
    
}
