package server.model.database.entity;

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
