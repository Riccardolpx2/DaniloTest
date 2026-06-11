/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.protocol.DTO;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Utente
 */
public class DomandaDTO implements Serializable{
    
    private String testoCifrato;
    private List<String> paroleCifrate;

    public DomandaDTO(String testoCifrato, List<String> paroleCifrate) {
        this.testoCifrato = testoCifrato;
        this.paroleCifrate=paroleCifrate;
    }
    
 

    public String getTestoCifrato() {
        return testoCifrato;
    }

    public void setTestoCifrato(String testoCifrato) {
        this.testoCifrato = testoCifrato;
    } 

    public List<String> getParoleCifrate() {
        return paroleCifrate;
    }

    public void setParoleCifrate(List<String> paroleCifrate) {
        this.paroleCifrate = paroleCifrate;
    }
    
}
