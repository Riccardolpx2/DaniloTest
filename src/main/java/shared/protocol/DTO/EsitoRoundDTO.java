package shared.protocol.DTO;

import java.io.Serializable;
import java.util.Map;

/**
 * Data Transfer Object (DTO) inviato dal Server alla chiusura di un round.
 * Fornisce informazioni su chi ha indovinato per primo, qual era la parola in chiaro corretta,
 * e contiene l'aggiornamento parziale dei punteggi della partita.
 */
public class EsitoRoundDTO implements Serializable {

    private String usernameVincitore;
    private String parolaSoluzione;

    // Mappa che associa l'username di ogni giocatore al suo punteggio attuale
    private Map<String, Integer> punteggi;

    /**
     * Costruisce il DTO con le informazioni sul round appena terminato.
     * @param usernameVincitore L'utente che ha vinto il round (può essere null in caso nessuno indovini in tempo).
     * @param parolaSoluzione La soluzione in chiaro attesa in questo round.
     * @param punteggi La situazione corrente dei punti accumulati.
     */
    public EsitoRoundDTO(String usernameVincitore, String parolaSoluzione, Map<String, Integer> punteggi) {
        this.usernameVincitore = usernameVincitore;
        this.parolaSoluzione = parolaSoluzione;
        this.punteggi = punteggi;
    }

    public String getUsernameVincitore() {
        return usernameVincitore;
    }

    public void setUsernameVincitore(String usernameVincitore) {
        this.usernameVincitore = usernameVincitore;
    }

    public String getParolaSoluzione() {
        return parolaSoluzione;
    }

    public void setParolaSoluzione(String parolaSoluzione) {
        this.parolaSoluzione = parolaSoluzione;
    }

    public Map<String, Integer> getPunteggi() {
        return punteggi;
    }

    public void setPunteggi(Map<String, Integer> punteggi) {
        this.punteggi = punteggi;
    }
}