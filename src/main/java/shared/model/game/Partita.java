/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.model.game;

import shared.model.Utente;

/**
 *
a * @author Utente
 */
public class Partita {
    private int idPartita;
    private int idSessione;
    private int offsetInizio;
    private int lunghezza;
    private int shiftCesare;
    private String parolaSoluzione;
    private int secondiRispostaG1;
    private int secondiRispostaG2;
    private int punteggioG1;
    private int punteggioG2;
    private String difficolta;
    private Utente vincitore;
    private Documento documento;

    public Partita(int idPartita, int idSessione, int offsetInizio, int lunghezza, int shiftCesare, String parolaSoluzione, int secondiRispostaG1, int secondiRispostaG2, int punteggioG1, int punteggioG2, String difficolta, Utente vincitore, Documento documento) {
        this.idPartita = idPartita;
        this.idSessione = idSessione;
        this.offsetInizio = offsetInizio;
        this.lunghezza = lunghezza;
        this.shiftCesare = shiftCesare;
        this.parolaSoluzione = parolaSoluzione;
        this.secondiRispostaG1 = secondiRispostaG1;
        this.secondiRispostaG2 = secondiRispostaG2;
        this.punteggioG1 = punteggioG1;
        this.punteggioG2 = punteggioG2;
        this.difficolta = difficolta;
        this.vincitore = vincitore;
        this.documento = documento;
    }
    
    

    public int getIdPartita() {
        return idPartita;
    }

    public void setIdPartita(int idPartita) {
        this.idPartita = idPartita;
    }

    public int getIdSessione() {
        return idSessione;
    }

    public void setIdSessione(int idSessione) {
        this.idSessione = idSessione;
    }

    public int getOffsetInizio() {
        return offsetInizio;
    }

    public void setOffsetInizio(int offsetInizio) {
        this.offsetInizio = offsetInizio;
    }

    public int getLunghezza() {
        return lunghezza;
    }

    public void setLunghezza(int lunghezza) {
        this.lunghezza = lunghezza;
    }

    public int getShiftCesare() {
        return shiftCesare;
    }

    public void setShiftCesare(int shiftCesare) {
        this.shiftCesare = shiftCesare;
    }

    public String getParolaSoluzione() {
        return parolaSoluzione;
    }

    public void setParolaSoluzione(String parolaSoluzione) {
        this.parolaSoluzione = parolaSoluzione;
    }

    public int getSecondiRispostaG1() {
        return secondiRispostaG1;
    }

    public void setSecondiRispostaG1(int secondiRispostaG1) {
        this.secondiRispostaG1 = secondiRispostaG1;
    }

    public int getSecondiRispostaG2() {
        return secondiRispostaG2;
    }

    public void setSecondiRispostaG2(int secondiRispostaG2) {
        this.secondiRispostaG2 = secondiRispostaG2;
    }

    public int getPunteggioG1() {
        return punteggioG1;
    }

    public void setPunteggioG1(int punteggioG1) {
        this.punteggioG1 = punteggioG1;
    }

    public int getPunteggioG2() {
        return punteggioG2;
    }

    public void setPunteggioG2(int punteggioG2) {
        this.punteggioG2 = punteggioG2;
    }

    public String getDifficolta() {
        return difficolta;
    }

    public void setDifficolta(String difficolta) {
        this.difficolta = difficolta;
    }

    public Utente getVincitore() {
        return vincitore;
    }

    public void setVincitore(Utente vincitore) {
        this.vincitore = vincitore;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    
            
}
