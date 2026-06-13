package shared.protocol.DTO;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) notificato dal Server ai Client per segnalare
 * che un matchmaking ha avuto successo e che la partita sta per cominciare.
 */
public class GameStartDTO implements Serializable {

    private String sfindateUsername;
    private String difficolta;

    /**
     * Costruisce le informazioni pre-partita.
     * @param sfindateUsername Il nome utente dell'avversario trovato per la sfida.
     * @param difficolta Il livello di difficoltà per la partita in procinto di iniziare.
     */
    public GameStartDTO(String sfindateUsername, String difficolta) {
        this.sfindateUsername = sfindateUsername;
        this.difficolta = difficolta;
    }

    public String getSfindateUsername() {
        return sfindateUsername;
    }

    public String getDifficolta() {
        return difficolta;
    }
}
