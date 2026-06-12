package shared.protocol.DTO;

import java.io.Serializable;
import java.util.Map;

public class EsitoRoundDTO implements Serializable {

    private String usernameVincitore;
    private String parolaSoluzione;

    // Mappa che associa l'username di ogni giocatore al suo punteggio attuale
    private Map<String, Integer> punteggi;

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