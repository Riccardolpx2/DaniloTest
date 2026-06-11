/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameLogic;

import java.util.List;

/**
 *
 * @author Utente
 */
public class Domanda {
    private String testoCifrato;
    private List<String> paroleSoluzioni;
    private List<String> paroleSoluzioniCifrate;
    private String difficolta;

    public Domanda(String testoCifrato, List<String> paroleSoluzioni, List<String> paroleSoluzioniCifrate, String difficolta) {
        this.testoCifrato = testoCifrato;
        this.paroleSoluzioni = paroleSoluzioni;
        this.paroleSoluzioniCifrate = paroleSoluzioniCifrate;
        this.difficolta = difficolta;
    }

    public String getTestoCifrato() {
        return testoCifrato;
    }

    public void setTestoCifrato(String testoCifrato) {
        this.testoCifrato = testoCifrato;
    }

    public List<String> getParoleSoluzioni() {
        return paroleSoluzioni;
    }

    public void setParoleSoluzioni(List<String> paroleSoluzioni) {
        this.paroleSoluzioni = paroleSoluzioni;
    }
    
    public List<String> getParoleSoluzioniCifrate() {
        return paroleSoluzioniCifrate;
    }

    public void setParoleSoluzioniCifrate(List<String> paroleSoluzioniCifrate) {
        this.paroleSoluzioniCifrate = paroleSoluzioniCifrate;
    }

    public String getDifficolta() {
        return difficolta;
    }

    public void setDifficolta(String difficolta) {
        this.difficolta = difficolta;
    }
    
    
}
