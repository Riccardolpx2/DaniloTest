package server.logica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import server.model.database.PartitaDAO;
import server.model.database.SessioneDiGiocoDAO;
import server.model.database.StatisticaDAO;
import server.model.database.entity.UtenteEntity;
import shared.game.AnalisiTesto;
import shared.game.Documento;
import shared.game.Partita;
import shared.game.SessioneDiGioco;
import shared.game.Statistica;
import shared.protocol.DTO.EsitoRoundDTO;
import shared.protocol.DTO.RispostaGiocatoreDTO;
import shared.protocol.DTO.TestoDTO;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MatchManager {

    private final SessioneDiGioco sessione;
    private final UtenteEntity giocatore1;
    private final UtenteEntity giocatore2;
    private final Documento documentoPartita;
    private boolean continua = true;

    private final PartitaDAO partitaDAO = new PartitaDAO();
    private final SessioneDiGiocoDAO sessioneDAO = new SessioneDiGiocoDAO();
    private final StatisticaDAO statisticaDAO = new StatisticaDAO();

    private int puntiG1;
    private int puntiG2;

    private final Map<String, Integer> mappaFrequenzaDocumento;
    private final String difficolta;

    private final Random random = new Random();
    private Partita roundCorrente; 
    
    private static final Map<String, String> dizionarioLemmi = caricaDizionarioLemmi();

    public MatchManager(UtenteEntity u1, UtenteEntity u2, Documento documentoPartita, String difficolta, AnalisiTesto analisi) {
        this.giocatore1 = u1;
        this.giocatore2 = u2;
        this.documentoPartita = documentoPartita;
        this.difficolta = difficolta;
        this.puntiG1 = 0;
        this.puntiG2 = 0;
        
        this.mappaFrequenzaDocumento = analisi.getFrequenzaParole();
        
        this.sessione = new SessioneDiGioco(
            0, 0, null, LocalDateTime.now(), 
            0, 0, null, u1, u2, "IN_CORSO"
        );
    }
    
    public TestoDTO inizializzaNuovoRound() {  
        // Trova la frequenza massima tra tutte le parole del documento
        int maxFrequenza = mappaFrequenzaDocumento.values().stream()
        .mapToInt(Integer::intValue).max().orElse(1);
        
        // Tiene solo le parole la cui frequenza rispetta la percentuale della difficoltà corrente.
        List<String> paroleFiltrate = mappaFrequenzaDocumento.entrySet().stream()
        .filter(entry -> rispettaSoglieUtente(entry.getValue(), maxFrequenza, difficolta))
        .map(Map.Entry::getKey).collect(Collectors.toList());

        if (paroleFiltrate.isEmpty()) {
            paroleFiltrate = new ArrayList<>(mappaFrequenzaDocumento.keySet());
        }
        
        String testoCompleto = this.documentoPartita.getTesto();
        int indiceParola = -1;
        String parolaSoluzioneCorrente = "";
        
        // Rimescola casualmente la lista delle parole che hanno superato il filtro della difficoltà
        Collections.shuffle(paroleFiltrate);
        
        // Scorre le parole rimescolate finché non ne trova una con occorrenze reali nel testo
        for (String parola : paroleFiltrate) {
            List<Integer> tutteLePosizioni = trovaTutteLeOccorrenze(testoCompleto, parola);
            if (!tutteLePosizioni.isEmpty()) {
                parolaSoluzioneCorrente = parola;
                // Se la parola compare più volte nel testo, ne estrae una posizione a sorte
                indiceParola = tutteLePosizioni.get(random.nextInt(tutteLePosizioni.size()));
                break; 
            }
        }
        
        /*se la ricerca fallisce, prende la prima parola della lista
                e ne cerca la prima occorrenza (case-insensitive) all'interno del testo*/
        if (indiceParola == -1) {
            parolaSoluzioneCorrente = paroleFiltrate.get(0);
            indiceParola = Math.max(0, testoCompleto.toLowerCase().indexOf(parolaSoluzioneCorrente.toLowerCase()));
        }
        
        // Genera una chiave di spostamento casuale per il Cifrario di Cesare (da 1 a 25)
        int shiftCorrente = random.nextInt(25) + 1;
        
        // Calcola i confini del testo da mostrare: circa 60 caratteri prima e dopo la parola target
        int inizioFrammento = Math.max(0, indiceParola - 250);
        int fineFrammento = Math.min(testoCompleto.length(), indiceParola + parolaSoluzioneCorrente.length() + 250);
        int lunghezzaFrammento = fineFrammento - inizioFrammento;
        
        // Inizializza l'oggetto Partita che rappresenta i dati di questo round nel DB
        this.roundCorrente = new Partita(0, this.sessione.getIdSessione(), inizioFrammento, 
            lunghezzaFrammento, shiftCorrente, parolaSoluzioneCorrente, 0, 0, difficolta, null, this.documentoPartita );
        
        // Estrae il pezzo di testo grezzo racchiuso nei confini calcolati
        String frammentoGrezzo = testoCompleto.substring(inizioFrammento, fineFrammento);
        // Pulisce il frammento: trasforma in minuscolo e sostituisce tutto ciò che non è una lettera con uno spazio
        String frammentoPulitoPerSplit = frammentoGrezzo.toLowerCase().replaceAll("[^a-zàèìòùáéíóú]", " ");
        // Spezza il testo pulito in un array di singole parole usando gli spazi come separatore
        String[] paroleNelFrammento = frammentoPulitoPerSplit.split("\\s+");
        
        // Crea la lista delle parole da cifrare, inserendo subito la soluzione 
        List<String> paroleDaCifrare = new ArrayList<>();
        paroleDaCifrare.add(parolaSoluzioneCorrente.toLowerCase()); 
        
        // Controlla ogni singola parola del frammento all'interno del dizionario dei lemmi
        for (String p : paroleNelFrammento) {
            if (!p.isEmpty()) {
                String lemmaDellaParola = dizionarioLemmi.get(p);
                /* Se la parola analizzata (es: "corro") ha lo stesso lemma della soluzione (es: "correre"),
            e non è già stata aggiunta, viene inserita nella lista delle varianti da oscurare */
                if (lemmaDellaParola != null && lemmaDellaParola.equalsIgnoreCase(parolaSoluzioneCorrente)) {
                    if (!paroleDaCifrare.contains(p)) {
                        paroleDaCifrare.add(p); 
                    }
                }
            }
        }

        paroleDaCifrare.sort((a, b) -> Integer.compare(b.length(), a.length()));
        
        // Divide il testo del libro in tre parti distinte rispetto alla parola target scelta per il round
        String testoPrecedente = testoCompleto.substring(inizioFrammento, indiceParola);
        String parolaOriginaleNelTesto = testoCompleto.substring(indiceParola, indiceParola + parolaSoluzioneCorrente.length());
        String testoSuccessivo = testoCompleto.substring(indiceParola + parolaSoluzioneCorrente.length(), fineFrammento);
        
        // Cifra la parola target principale con il Cifrario di Cesare e la aggiunge alla lista del DTO
        List<String> paroleOscurate = new ArrayList<>();
        paroleOscurate.add(cifraCesare(parolaOriginaleNelTesto, this.roundCorrente.getShiftCesare()));
        
        /*Cicla tutte le varianti collegate (i verbi simili) ed esegue una sostituzione 
        chirurgica tramite Regex sia nel blocco di testo precedente che in quello successivo */
        for (String variante : paroleDaCifrare) {
            testoPrecedente = rimpiazza(testoPrecedente, variante);
            testoSuccessivo = rimpiazza(testoSuccessivo, variante);
        }
        // Impacchetta le tre porzioni di testo elaborate e le restituisce al Client
        return new TestoDTO(testoPrecedente, paroleOscurate, testoSuccessivo);
    }
    
    //Regex non va bene la funzione contains
    private String rimpiazza(String testo, String variante) {
        String regex = "(?i)(?<=^|[^a-zàèìòùáéíóú])" + Pattern.quote(variante) + "(?=[^a-zàèìòùáéíóú]|$)";
        Pattern pattern = java.util.regex.Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testo);

        StringBuffer sb = new StringBuffer(); 
        while (matcher.find()) {
            String parolaOriginaleTrovata = matcher.group();
            String parolaCifrata = cifraCesare(parolaOriginaleTrovata, this.roundCorrente.getShiftCesare());
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(parolaCifrata));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private List<Integer> trovaTutteLeOccorrenze(String testo, String parola) {
        // Lista dinamica che conterrà gli indici (le posizioni numeriche) in cui viene trovata la parola
        List<Integer> posizioni = new ArrayList<>();
        // ignora maiuscole/minuscole
        String testoLower = testo.toLowerCase();
        String parolaLower = parola.toLowerCase();
        
        // Cerca la PRIMA occorrenza della parola all'inizio di tutto il testo.
        int index = testoLower.indexOf(parolaLower);
        while (index != -1) {
            // Aggiunge l'indice appena trovato alla lista delle posizioni valide
            posizioni.add(index);
            // continua a cercare nel resto del libro
            index = testoLower.indexOf(parolaLower, index + 1); 
        }
        return posizioni;
    }
    
    private boolean rispettaSoglieUtente(int frequenza, int maxFrequenza, String difficolta) {
        // Calcola la percentuale di diffusione della parola rispetto alla parola più comune del libro
        double percentuale = (double) frequenza / maxFrequenza * 100;
        boolean risultato = false;
        // Struttura di controllo switch per verificare i criteri in base alla difficoltà selezionata
        switch (difficolta.toUpperCase()) {
            case "FACILE":
                risultato = (percentuale >= 50.0);
                break;
            case "MEDIA":
                risultato = (percentuale >= 10.0 && percentuale <= 40.0);
                break;
            case "DIFFICILE":
                risultato = (percentuale < 10.0);
                break;
            default:
                // Clausola di sicurezza
                risultato = false;
                break;
        }
        return risultato;
    }
    
    private String cifraCesare(String parola, int shift) {
        StringBuilder risultato = new StringBuilder();
        
        // Scompone la parola in un array di singoli caratteri
        for (char carattere : parola.toCharArray()) {
            //cifra il carattere SOLO se si tratta di una lettera
            if (Character.isLetter(carattere)) {
                // Determina il punto di partenza nell'esadecimale/ASCII
                char base = Character.isUpperCase(carattere) ? 'A' : 'a';
                // LA FORMULA MATEMATICA DI CESARE
                char carattereCifrato = (char) ((carattere - base + shift) % 26 + base);
                // Aggiunge la lettera cifrata alla nostra stringa in costruzione
                risultato.append(carattereCifrato);
            } else {            
                // se il carattere non è una lettera, lo lascia esattamente così com'è senza cifrarlo
                risultato.append(carattere); 
            }   
        }
        return risultato.toString();
    } 
    
    public void registraRispostaNuovaPartita(boolean rispG1, boolean rispG2) {
        if (!rispG1 || !rispG2) {
            this.continua = false;
        }
    }
     
    public boolean isSessioneFinita() {
        return !this.continua;
    }
    
    public UtenteEntity determinaVincitoreRound(String rispostaG1, int tempoG1, String rispostaG2, int tempoG2) {
        String soluzione = this.roundCorrente.getParolaSoluzione();
        boolean g1Corretto = rispostaG1.equalsIgnoreCase(soluzione);
        boolean g2Corretto = rispostaG2.equalsIgnoreCase(soluzione);
        
        if (!g1Corretto && !g2Corretto) return null; 

        if (g1Corretto && !g2Corretto) return this.giocatore1;

        if (!g1Corretto && g2Corretto) return this.giocatore2;

        if (tempoG1 < tempoG2) return this.giocatore1;
        else if (tempoG2 < tempoG1) return this.giocatore2;
        return null;
    }
    
    public EsitoRoundDTO registraEsitoRound(RispostaGiocatoreDTO rispG1, RispostaGiocatoreDTO rispG2) throws SQLException {
        // feterminazione vincitore
        UtenteEntity vincitoreRound = determinaVincitoreRound(rispG1.getParolaTentata(), rispG1.getTempo(), rispG2.getParolaTentata(), rispG2.getTempo());
        
        // aggiornamenti punteggi in memoria
        if (vincitoreRound != null) {
            if (vincitoreRound.getUsername().equals(this.giocatore1.getUsername())) {
                this.puntiG1++;
                this.sessione.incrementaPunteggioG1(1);
            } else {
                this.puntiG2++;
                this.sessione.incrementaPunteggioG2(1);
            }
        }
        // Salva i secondi impiegati da entrambi i giocatori per rispondere
        this.roundCorrente.setSecondiRispostaG1(rispG1.getTempo());
        this.roundCorrente.setSecondiRispostaG2(rispG2.getTempo());
        // Salva il riferimento dell'utente vincitore
        this.roundCorrente.setVincitore(vincitoreRound);
        
        // Inserisce il record del round appena concluso nella tabella 'partite' del Database
        partitaDAO.aggiungi(this.roundCorrente);
        //aggiunge il round alla sessione
        this.sessione.aggiungiPartita(this.roundCorrente);
        
        // Se c'è un vincitore prende il suo username, altrimenti imposta la stringa "Pareggio"
        String nomeVincitore = (vincitoreRound != null) ? vincitoreRound.getUsername() : "Pareggio";
        // restituisce il DTO pronto per essere inviato
        return new EsitoRoundDTO(nomeVincitore, this.roundCorrente.getParolaSoluzione(), this.puntiG1, this.puntiG2);
    }
    
    public void terminaSessione() throws SQLException {
        this.sessione.setStato("TERMINATA");
        
        UtenteEntity vincitore = null;
        UtenteEntity perdente = null;

        if (this.puntiG1 > this.puntiG2) {
            vincitore = this.giocatore1;
            perdente = this.giocatore2;
        } else if (this.puntiG2 > this.puntiG1) {
            vincitore = this.giocatore2;
            perdente = this.giocatore1;
        } 
        this.sessione.setVincitore(vincitore);
        sessioneDAO.aggiorna(this.sessione);

        if (vincitore != null && perdente != null) {
            double tempoMedioSessioneG1 = this.sessione.getPartite().stream()
                    .mapToInt(partita -> partita.getSecondiRispostaG1())
                    .average().orElse(0.0);

            double tempoMedioSessioneG2 = this.sessione.getPartite().stream()
                    .mapToInt(partita -> partita.getSecondiRispostaG2())
                    .average().orElse(0.0);

            boolean g1HaVinto = vincitore.getUsername().equals(giocatore1.getUsername());
            aggiornaStatisticheStoriche(giocatore1, g1HaVinto, tempoMedioSessioneG1);
            aggiornaStatisticheStoriche(giocatore2, !g1HaVinto, tempoMedioSessioneG2);
        }
    }

    private void aggiornaStatisticheStoriche(UtenteEntity utente, boolean haVinto, double tempoMedioSessione) throws SQLException {
        Statistica stat = statisticaDAO.cerca(utente.getUsername());
        boolean isNuovo = false;
        
        if (stat == null) {
            stat = new Statistica(utente, 0, 0, 0, tempoMedioSessione);
            isNuovo = true;
        }
        
        int matchPrecedenti = stat.getVittorie() + stat.getSconfitte();
        double vecchiaMedia = stat.getMediaRisposta();

        if (haVinto) {
            stat.setVittorie(stat.getVittorie() + 1);
        } else {
            stat.setSconfitte(stat.getSconfitte() + 1);
        }
        
        int totali = stat.getVittorie() + stat.getSconfitte();
        stat.setPercentualeVittorie((stat.getVittorie() * 100) / totali);

        if (!isNuovo) {
            double nuovaMedia = ((vecchiaMedia * matchPrecedenti) + tempoMedioSessione) / totali;
            stat.setMediaRisposta(nuovaMedia);
        }

        if (isNuovo) {
            statisticaDAO.aggiungi(stat);
        } else {
            statisticaDAO.aggiorna(stat);
        }
    }
      
    private static Map<String, String> caricaDizionarioLemmi() {
        Map<String, String> mappa = new HashMap<>();
        try {
            InputStream is = MatchManager.class.getResourceAsStream("/txt/lemmi.txt");
            if (is == null) {
                System.out.println("AVVISO: File lemmi.txt non trovato in resources.");
                return mappa;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                mappa = reader.lines().map(String::trim)                                           
                .filter(linea -> !linea.isEmpty() && !linea.startsWith("#")) 
                .map(linea -> linea.split("\\s+")).filter(parti -> parti.length >= 2)              
                .collect(Collectors.toMap(                
                    parti -> parti[0].toLowerCase(),     
                    parti -> parti[1].toLowerCase(),                      
                    (vecchio, nuovo) -> vecchio                              
                ));
            }
        } catch (IOException e) {
            System.out.println("Errore nel caricamento del dizionario dei lemmi: " + e.getMessage());
        }
        return mappa;
    }
    public Partita getRoundCorrente() { // Sostituisci "Round" col tipo esatto della tua variabile
    return this.roundCorrente;
}
}