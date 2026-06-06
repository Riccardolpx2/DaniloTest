package shared.protocol.DTO;

import java.io.Serializable;

public class LoginDTO implements Serializable {
    private String username;
    private String password;

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
