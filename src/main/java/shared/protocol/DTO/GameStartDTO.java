package shared.protocol.DTO;

import java.io.Serializable;

public class GameStartDTO implements Serializable {

    private String sfindateUsername;
    private String difficolta;

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
