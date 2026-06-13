/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameLogic;

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
 * Gestisce l'elaborazione statistica dei testi dei documenti di gioco.
 * Si occupa di filtrare le parole non rilevanti (stop-words), ripulire il testo da punteggiatura
 * e calcolare la frequenza di occorrenza delle parole significative per la generazione degli enigmi.
 * @author Utente
 */
    public class AnalisiTesto{
    private int idDocumento;
    private Map<String, Integer> frequenzaParole; 
    /** Set statico e immutabile contenente le parole irrilevanti da scartare (articoli, preposizioni, ecc.) */
    private static final Set<String> stopWords = caricaStopWords();

    /**
     * Costruttore: inizializza il gestore dell'analisi per uno specifico documento.
     * Prepara la mappa interna che conterrà le frequenze.
     * @param idDocumento L'identificativo del documento da analizzare o associato.
     */
    public AnalisiTesto(int idDocumento) {
        this.idDocumento = idDocumento;
        this.frequenzaParole = new HashMap<>();
    }

    /**
     * Permette di inserire una coppia parola-frequenza direttamente nella mappa.
     * Utilizzato dall'AnalisiTestoDAO quando ricostruisce l'oggetto dal database.
     * @param parola La parola target.
     * @param frequenza Il numero di occorrenze di quella parola nel documento.
     */
    public void aggiungiParolaFrequenza(String parola, int frequenza) {
        this.frequenzaParole.put(parola, frequenza);
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

    /**
     * Carica il file delle stop-words.
     * Esegue la pulizia di ogni riga eliminando commenti (righe che iniziano con #) e spazi vuoti.
     * @return Un Set di stringhe contenente tutte le stop-words caricate, oppure un Set vuoto in caso di errore.
     */
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

    /**
     * Esegue l'analisi testuale vera e propria su una stringa in chiaro.
     * Il processo prevede:
     * Conversione in minuscolo e rimozione di caratteri non alfabetici tramite Regex.
     * Divisione in token (parole) sugli spazi bianchi.
     * Filtraggio delle stringhe vuote, delle parole con lunghezza minore uguale 3 caratteri e delle stop-words.
     * Raggruppamento in una mappa delle frequenze.
     *  @param testo Il testo in chiaro del documento da analizzare.
     */
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
