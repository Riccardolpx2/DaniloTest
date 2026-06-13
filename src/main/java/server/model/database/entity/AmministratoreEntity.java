package server.model.database.entity;

/**
 * Rappresenta l'entità di dominio Amministratore mappata direttamente sulla tabella amministratori del database.
 * Questa classe definisce il modello dei dati minimo per identificare e autenticare gli utenti con 
 * privilegi amministrativi all'interno del sistema, separandoli loggicamente dai normali giocatori.
 */
public class AmministratoreEntity {
    private String password;
    private String username;

    public AmministratoreEntity(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public String getUsername() {
        return username;
    }

}
