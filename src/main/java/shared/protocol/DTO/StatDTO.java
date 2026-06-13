package shared.protocol.DTO;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) inviato dal Server in risposta ad una richiesta
 * di statistiche dello storico personale del giocatore.
 */
public class StatDTO implements Serializable {
    private String username;

    private int vittorie;
    private int sconfitte;
    private int percentualeVittorie;
    private double mediaRisposta;

    /**
     * Costruisce le statistiche storiche di un utente.
     * @param username L'identificativo del giocatore.
     * @param vittorie Numero totale di match vinti.
     * @param sconfitte Numero totale di match persi.
     * @param percentualeVittorie Calcolo intero indicante la W/L ratio percentuale.
     * @param mediaRisposta Il tempo medio impiegato a rispondere (in secondi).
     */
    public StatDTO(String username, int vittorie, int sconfitte, int percentualeVittorie, double mediaRisposta) {
        this.username = username;
        this.vittorie = vittorie;
        this.sconfitte = sconfitte;
        this.percentualeVittorie = percentualeVittorie;
        this.mediaRisposta = mediaRisposta;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVittorie() {
        return vittorie;
    }

    public void setVittorie(int vittorie) {
        this.vittorie = vittorie;
    }

    public int getSconfitte() {
        return sconfitte;
    }

    public void setSconfitte(int sconfitte) {
        this.sconfitte = sconfitte;
    }

    public int getPercentualeVittorie() {
        return percentualeVittorie;
    }

    public void setPercentualeVittorie(int percentualeVittorie) {
        this.percentualeVittorie = percentualeVittorie;
    }

    public double getMediaRisposta() {
        return mediaRisposta;
    }

    public void setMediaRisposta(double mediaRisposta) {
        this.mediaRisposta = mediaRisposta;
    }
}
