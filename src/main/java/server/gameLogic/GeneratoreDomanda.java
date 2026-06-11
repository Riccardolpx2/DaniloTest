package server.gameLogic;

import shared.game.AnalisiTesto;
import shared.game.Documento;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class GeneratoreDomanda {

    private final Random random = new Random();

    // carica il dizionario una sola volta
    private static final Map<String, String> DIZIONARIO_LEMMI = caricaDizionarioLemmi();

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
             // shift casuale per cifratura Cesare
            int shift = random.nextInt(25) + 1;
            
            // crea un testo attorno alla parola (±250 caratteri)
            int start = Math.max(0, index - 250);
            int end = Math.min(testo.length(), index + parola.length() + 250);

            String frammento = testo.substring(start, end);
            
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
            domande.add(new Domanda(testoCifrato,paroleDaCifrare,paroleCifrate,difficolta)
            );
            generate++;
        }

        return domande;
    }


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
}