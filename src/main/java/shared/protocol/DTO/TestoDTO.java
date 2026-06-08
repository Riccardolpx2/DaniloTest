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
public class TestoDTO implements Serializable{
    
    private String testoPrecedente;
    private List<String> paroleOscurate;
    private String testoSuccessivo;

    public TestoDTO(String testoPrecedente, List<String> paroleOscurate, String testoSuccessivo) {
        this.testoPrecedente = testoPrecedente;
        this.paroleOscurate = paroleOscurate;
        this.testoSuccessivo = testoSuccessivo;
    }

    public String getTestoPrecedente() {
        return testoPrecedente;
    }

    public void setTestoPrecedente(String testoPrecedente) {
        this.testoPrecedente = testoPrecedente;
    }

    public List<String> getParoleOscurate() {
        return paroleOscurate;
    }

    public void setParoleOscurate(List<String> paroleOscurate) {
        this.paroleOscurate = paroleOscurate;
    }

    public String getTestoSuccessivo() {
        return testoSuccessivo;
    }

    public void setTestoSuccessivo(String testoSuccessivo) {
        this.testoSuccessivo = testoSuccessivo;
    }

    
}
