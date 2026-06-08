/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.protocol.DTO;

import java.io.Serializable;

/**
 *
 * @author Utente
 */
public class EsitoRoundDTO implements Serializable{
    
    private String usernameVincitore; 
    private String parolaSoluzione;   
    private int punteggioAttualeG1;
    private int punteggioAttualeG2;

    public EsitoRoundDTO(String usernameVincitore, String parolaSoluzione, int punteggioAttualeG1, int punteggioAttualeG2) {
        this.usernameVincitore = usernameVincitore;
        this.parolaSoluzione = parolaSoluzione;
        this.punteggioAttualeG1 = punteggioAttualeG1;
        this.punteggioAttualeG2 = punteggioAttualeG2;
    }

    public String getUsernameVincitore() {
        return usernameVincitore;
    }

    public void setUsernameVincitore(String usernameVincitore) {
        this.usernameVincitore = usernameVincitore;
    }

    public String getParolaSoluzione() {
        return parolaSoluzione;
    }

    public void setParolaSoluzione(String parolaSoluzione) {
        this.parolaSoluzione = parolaSoluzione;
    }

    public int getPunteggioAttualeG1() {
        return punteggioAttualeG1;
    }

    public void setPunteggioAttualeG1(int punteggioAttualeG1) {
        this.punteggioAttualeG1 = punteggioAttualeG1;
    }

    public int getPunteggioAttualeG2() {
        return punteggioAttualeG2;
    }

    public void setPunteggioAttualeG2(int punteggioAttualeG2) {
        this.punteggioAttualeG2 = punteggioAttualeG2;
    }
    
    
    
}
