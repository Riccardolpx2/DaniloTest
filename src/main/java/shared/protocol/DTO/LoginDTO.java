package shared.protocol.DTO;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) che incapsula le credenziali di accesso
 * inviate dal client al server per richiedere l'autenticazione.
 */
public class LoginDTO implements Serializable {
    private String username;
    private String password;

    /**
     * Inizializza il payload del login.
     * @param username Il nome utente.
     * @param password La password in chiaro inserita nel form.
     */
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
