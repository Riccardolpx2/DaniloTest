/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameUtil;

import java.util.List;

/**
 * Rappresenta la struttura di una singola domanda all'interno del gioco.
 * Contiene i riferimenti al testo cifrato da mostrare e le relative soluzioni, sia in chiaro
 * (per la verifica delle risposte) sia cifrate ( necessarie alla logica di gioco).
 *  @author Utente
 */
public class Domanda {
    private int idDomanda;
    private int idDocumento;
    private String testoCifrato;
    private List<String> paroleSoluzioni;
    private List<String> paroleSoluzioniCifrate;
    private String difficolta;
    
    /**
     * Costruttore completo per l'inizializzazione di una domanda.
     * Viene invocato solitamente dal DAO o dal gestore delle domande quando carica i dati dal DB.
     * @param idDomanda L'identificativo univoco della domanda.
     * @param idDocumento L'ID del documento di origine associato alla domanda.
     * @param testoCifrato Il testo principale dell'enigma in formato cifrato.
     * @param paroleSoluzioni La lista delle risposte corrette accettate in chiaro.
     * @param paroleSoluzioniCifrate La lista delle risposte corrette in formato cifrato.
     * @param difficolta Il livello di difficoltà associato (es. "FACILE", "MEDIA", "DIFFICILE").
     */
    public Domanda(int idDomanda,int idDocumento,String testoCifrato, List<String> paroleSoluzioni, List<String> paroleSoluzioniCifrate, String difficolta) {
        this.idDomanda=idDomanda;
        this.idDocumento=idDocumento;
        this.testoCifrato = testoCifrato;
        this.paroleSoluzioni = paroleSoluzioni;
        this.paroleSoluzioniCifrate = paroleSoluzioniCifrate;
        this.difficolta = difficolta;
    }

    public int getIdDomanda() {
        return idDomanda;
    }

    public void setIdDomanda(int idDomanda) {
        this.idDomanda = idDomanda;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
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
