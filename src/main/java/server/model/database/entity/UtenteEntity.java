package server.model.database.entity;

/**
 * Rappresenta l'entità di dominio Utente mappata direttamente sulla tabella utenti del database.
 * Questa classe memorizza e veicola i dati anagrafici e le credenziali di 
 * accesso di un giocatore all'interno dell'applicazione.
 * @author Utente
 */
public class UtenteEntity {
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String dataNascita;
    
    /**
     * Costruttore completo per istanziare un oggetto UtenteEntity con tutti i suoi attributi.
     * @param username    Lo username univoco dell'utente.
     * @param password    La password di accesso.
     * @param nome        Il nome dell'utente.
     * @param cognome     Il cognome dell'utente.
     * @param dataNascita La data di nascita dell'utente.
     */
    public UtenteEntity(String username, String password, String nome, String cognome, String dataNascita) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", dataNascita='" + dataNascita + '\'' +
                '}';
    }
}
