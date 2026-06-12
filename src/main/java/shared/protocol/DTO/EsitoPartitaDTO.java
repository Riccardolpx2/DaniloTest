package shared.protocol.DTO;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO che incapsula il recap finale della partita.
 */
public class EsitoPartitaDTO implements Serializable {

    private String usernameVincitore; // Può essere null se finiscono pari
    private Map<String, Integer> punteggiFinali;
    private boolean terminataPerAbbandono;  // // serve perché il vincitore potrebbe avere meno punti se l'altro ha abbandonato

    public EsitoPartitaDTO(String usernameVincitore, Map<String, Integer> punteggiFinali,
                           boolean terminataPerAbbandono) {
        this.usernameVincitore = usernameVincitore;
        this.punteggiFinali = punteggiFinali;
        this.terminataPerAbbandono = terminataPerAbbandono;
    }

    public String getUsernameVincitore() { return usernameVincitore; }
    public void setUsernameVincitore(String usernameVincitore) { this.usernameVincitore = usernameVincitore; }

    public Map<String, Integer> getPunteggiFinali() { return punteggiFinali; }
    public void setPunteggiFinali(Map<String, Integer> punteggiFinali) { this.punteggiFinali = punteggiFinali; }

    public boolean isTerminataPerAbbandono() { return terminataPerAbbandono; }
    public void setTerminataPerAbbandono(boolean terminataPerAbbandono) { this.terminataPerAbbandono = terminataPerAbbandono; }
}