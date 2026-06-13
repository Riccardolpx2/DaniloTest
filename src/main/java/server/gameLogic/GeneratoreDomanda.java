package server.gameLogic;

import server.gameUtil.Domanda;
import server.gameUtil.AnalisiTesto;
import server.gameUtil.Documento;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Componente incaricato della generazione dinamica delle domande.
 * Estrae le parole da un testo in base a soglie percentuali di frequenza statistica (difficoltà), 
 * ne isola il contesto (frammento circostante), effettua la lemmatizzazione per rintracciare 
 * le varianti flessive dello stesso termine e maschera il testo applicando un Cifrario di Cesare 
 * con un fattore di spostamento (shift) casuale.
 * @author Utente
 */
public class GeneratoreDomanda {

    private final Random random = new Random();

    /**Dizionario di lemmatizzazione (mappa parola , lemma di riferimento). 
     * Caricato staticamente in memoria all'avvio della classe per ottimizzare le prestazioni.
     */
    private static final Map<String, String> DIZIONARIO_LEMMI = caricaDizionarioLemmi();

    /**
     * Genera un elenco di domande cifrate basate sulle metriche di un documento e della sua analisi.
     * Il processo filtra le parole candidate per difficoltà, ne seleziona una quantità definita in modo randomico,
     * isola una finestra di caratteri attorno al termine nel testo originale e cifra sia la parola target 
     * che tutte le sue varianti flessive (stesso lemma) trovate nel medesimo frammento.
     * @param quantita   Il numero di domande da generare [fisso per difficoltà].
     * @param difficolta Il livello di difficoltà richiesto ("FACILE", "MEDIA", "DIFFICILE").
     * @param documento  Il {@link Documento} contenente il testo letterale di partenza.
     * @param analisi    L'oggetto {@link AnalisiTesto} contenente la mappa delle frequenze delle parole.
     * @return Una {@link List} di oggetti {@link Domanda} pronti per essere somministrati o salvati.
     */
    public List<Domanda> creaDomande(int quantita, String difficolta, Documento documento, AnalisiTesto analisi) {

        List<Domanda> domande = new ArrayList<>();

        Map<String, Integer> frequenze = analisi.getFrequenzaParole();
        String testo = documento.getTesto();

        // Trova la parola più frequente
        int maxFreq = frequenze.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        
        // filtro parole per difficoltà
        List<String> paroleC = frequenze.entrySet()
                .stream().filter(e -> rispettaSoglie(e.getValue(), maxFreq, difficolta))
                .map(Map.Entry::getKey).collect(Collectors.toList());
        
         //se il filtro elimina tutto, usa tutte le parole
        if (paroleC.isEmpty()) {
            paroleC = new ArrayList<>(frequenze.keySet());
        }
        
        // mescola per randomizzare la selezione delle parole
        Collections.shuffle(paroleC);

        int generate = 0;
        
        // ciclo finché non genero abbastanza domande
        for (String parola : paroleC) {

            if (generate >= quantita) break;
            // trova tutte le occorrenze della parola nel testo
            List<Integer> posizioni = trovaOccorrenze(testo, parola);

            if (posizioni.isEmpty()) continue;
             // scegli una occorrenza casuale 
            int index = posizioni.get(random.nextInt(posizioni.size()));
            
            String frammento = estraiContestoFrase(testo, index, parola);
            if (frammento.isEmpty()) continue;
            
             // shift casuale per cifratura Cesare
            int shift = random.nextInt(25) + 1;
            
            //pulizia per analisi parole
            String testoPulito = frammento.toLowerCase().replaceAll("[^a-zàèìòù]", " ");

            String[] parole = testoPulito.split("\\s+");
            
            // lista parole da cifrare
            List<String> paroleDaCifrare = new ArrayList<>();
            paroleDaCifrare.add(parola.toLowerCase());
            
            //serve per trovare varianti della parola
            String lemmaTarget =DIZIONARIO_LEMMI.getOrDefault(parola, parola);
            
            // cerca parole con stesso lemma nel frammento
            for (String p : parole) {
                if (p.isEmpty()) continue;
                String lemmaC = DIZIONARIO_LEMMI.getOrDefault(p, p);
                // se condividono il lemma, sono varianti della stessa parola
                if (lemmaC.equalsIgnoreCase(lemmaTarget)) {
                    if (!paroleDaCifrare.contains(p)) {
                        paroleDaCifrare.add(p);
                    }
                }
            }
            // ordina per lunghezza (evita problemi di sostituzione parziale)
            paroleDaCifrare.sort((a, b) -> Integer.compare(b.length(), a.length()));

            List<String> paroleCifrate = new ArrayList<>();
            String testoCifrato = frammento;
            
            // cifratura del testo e sostituzione delle parole
            for (String v : paroleDaCifrare) {
                // cifratura Cesare della parola
                String cifrata = cifraCesare(v, shift);
                // salva versione cifrata
                paroleCifrate.add(cifrata);
                
                  // sostituzione nel testo
                testoCifrato = testoCifrato.replaceAll("(?i)\\b" + v + "\\b",cifrata);
            }          
            // crea domanda finale
            domande.add(new Domanda(0,documento.getIdDocumento(), testoCifrato,paroleDaCifrare,paroleCifrate,difficolta)
            );
            generate++;
        }

        return domande;
    }

    /**
     * Applica l'algoritmo del Cifrario di Cesare su una stringa di input.
     * Esegue uno slittamento dei soli caratteri alfabetici preservando la distinzione 
     * tra maiuscole e minuscole mediante aritmetica modulare su base 26. I caratteri speciali 
     * o accentati non alfabetici standard vengono lasciati inalterati.
     * @param parola La stringa testuale in chiaro da cifrare.
     * @param shift  Il fattore di spostamento (chiave di cifratura).
     * @return La stringa risultante interamente cifrata.
     */
    private String cifraCesare(String parola, int shift) {

        StringBuilder sb = new StringBuilder();
        for (char c : parola.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                sb.append((char) ((c - base + shift) % 26 + base));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Valuta se la frequenza di una parola soddisfa i requisiti percentuali della difficoltà di gioco.
     * FACILE: Parole molto comuni (frequenza relativa &ge; 50%)
     * MEDIA: Parole mediamente comuni (frequenza relativa compresa tra 10% e 40%)
     * DIFFICILE: Parole rare (frequenza relativa &lt; 10%)
     * @param freq    La frequenza assoluta della parola in esame.
     * @param maxFreq La frequenza assoluta della parola più ricorrente nell'intero documento.
     * @param diff    Il livello di difficoltà richiesto in formato testuale.
     * @return true se il termine rispetta il range stabilito dalla difficoltà, false altrimenti.
     */
    private boolean rispettaSoglie(int freq, int maxFreq,String diff) {

   double p = (double) freq / maxFreq * 100;
    boolean risultato;

    switch (diff.toUpperCase()) {
        case "FACILE":
            risultato = (p >= 50);
            break;

        case "MEDIA":
            risultato = (p >= 10 && p <= 40);
            break;

        case "DIFFICILE":
            risultato = (p < 10);
            break;

        default:
            risultato = false;
            break;
    }

    return risultato;
    }
    
    /**
     * Scansiona il testo per identificare gli indici di posizionamento iniziale di una parola.
     * @param testo  Il corpo del testo in cui cercare.
     * @param parola La parola da cercare all'interno del testo.
     * @return Una {@link List} di {@link Integer} contenente gli indici di partenza di ogni occorrenza trovata.
     */
    private List<Integer> trovaOccorrenze(String testo, String parola) {

        List<Integer> pos = new ArrayList<>();
        String t = testo.toLowerCase();
        String p = parola.toLowerCase();

        int idx = t.indexOf(p);
        while (idx != -1) {
            pos.add(idx);
            idx = t.indexOf(p, idx + 1);
        }
        return pos;
    }

    /**
     * Carica il file di risorsa del dizionario morfologico e compila la mappa dei lemmi.
     * Legge il file /txt/lemmi.txt esclude le righe vuote 
     * o i commenti (identificati da '#') ed estrae le coppie formata da forma flessa e lemma.
     * In caso di chiavi duplicate, mantiene la prima occorrenza incontrata.
     * * @return Una {@link Map} contenente le associazioni chiave-valore tra parole e lemmi. 
     * In caso di file mancante o errori di I/O restituisce una mappa vuota.
     */
    private static Map<String, String> caricaDizionarioLemmi() {
        Map<String, String> mappa = new HashMap<>();
        try {
            InputStream is = GeneratoreDomanda.class.getResourceAsStream("/txt/lemmi.txt");
            if (is == null) return mappa;
            try (BufferedReader br = new BufferedReader( new InputStreamReader(is,StandardCharsets.UTF_8))) {
                mappa = br.lines().map(String::trim).filter(l -> !l.isEmpty() && !l.startsWith("#"))
                        .map(l -> l.split("\\s+")).filter(a -> a.length >= 2).collect(Collectors.toMap(a -> a[0].toLowerCase(),
                        a -> a[1].toLowerCase(),(a, b) -> a));
            }
        } catch (IOException e) {
            System.out.println("Errore lemmi: " + e.getMessage());
        }

        return mappa;
    }
    
    /**
     * Isola un frammento di testo attorno a una parola target basandosi sui confini naturali delle frasi (. ! ?).
     * Se la singola frase in cui risiede la parola è inferiore a 150 caratteri, espande dinamicamente il contesto 
     * includendo anche la frase precedente e quella successiva.
     * @param testo   Il testo completo del documento di origine.
     * @param index   L'indice assoluto in cui inizia la parola rilevata.
     * @param parola  La stringa della parola selezionata.
     * @return Il frammento di testo ottimizzato e rifinito.
     */
    private String estraiContestoFrase(String testo, int index, String parola) {
        // 1. Cerca l'inizio della frase corrente
        int start = index;
        while (start > 0) {
            char c = testo.charAt(start - 1);
            if (c == '.' || c == '!' || c == '?') {
                break;
            }
            start--;
        }

        // Cerca la fine della frase corrente
        int end = index + parola.length();
        while (end < testo.length()) {
            char c = testo.charAt(end);
            if (c == '.' || c == '!' || c == '?') {
                end++; // Include il carattere di punteggiatura finale
                break;
            }
            end++;
        }

        // Controllo di elasticità sulla lunghezza (soglia minima: 150 caratteri)
        if ((end - start) < 150) {
            // Espansione sulla frase precedente
            int extraStart = start - 1;
            while (extraStart > 0) {
                char c = testo.charAt(extraStart - 1);
                if (c == '.' || c == '!' || c == '?') {
                    start = extraStart;
                    break;
                }
                extraStart--;
            }
            if (extraStart == 0) start = 0;

            // Espansione sulla frase successiva
            int extraEnd = end;
            while (extraEnd < testo.length()) {
                char c = testo.charAt(extraEnd);
                if (c == '.' || c == '!' || c == '?') {
                    end = extraEnd + 1;
                    break;
                }
                extraEnd++;
            }
            if (extraEnd == testo.length()) end = testo.length();
        }

        return testo.substring(start, end).trim();
    }
}