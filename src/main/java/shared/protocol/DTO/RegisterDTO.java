package shared.protocol.DTO;

import java.io.Serializable;

public class RegisterDTO implements Serializable {
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String dataNascita;

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
