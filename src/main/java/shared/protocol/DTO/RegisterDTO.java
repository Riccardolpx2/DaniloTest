package shared.protocol.DTO;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) utilizzato dal Client per inviare i dati
 * anagrafici e le nuove credenziali richieste in fase di registrazione account.
 */
public class RegisterDTO implements Serializable {
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String dataNascita;

    /**
     * Popola i dettagli anagrafici e di sicurezza per un nuovo account.
     * @param username L'identificativo scelto dall'utente.
     * @param password La password in chiaro da salvare.
     * @param nome Il nome proprio dell'utente.
     * @param cognome Il cognome dell'utente.
     * @param dataNascita La data di nascita dell'utente formattata come stringa.
     */
    public RegisterDTO(String username, String password, String nome, String cognome, String dataNascita) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
    }

    public String getUsername() {
        return username;
    }

    public String getCognome() {
        return cognome;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }


}
