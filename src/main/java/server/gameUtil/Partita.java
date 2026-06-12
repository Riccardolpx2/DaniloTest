/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameUtil;

import server.model.database.entity.UtenteEntity;

/**
 *
a * @author Utente
 */
public class Partita {
    private int idPartita;
    private int idSessione;
    private int offsetIniziale;
    private int lunghezza;
    private int shiftCesare;
    private String parolaSoluzione;
    private int secondiRispostaG1;
    private int secondiRispostaG2;

    private String difficolta;
    private UtenteEntity vincitore;
    private Documento documento;

    public Partita(int idPartita, int idSessione, int offsetIniziale, int lunghezza, int shiftCesare, 
            String parolaSoluzione, int secondiRispostaG1, int secondiRispostaG2, String difficolta, UtenteEntity vincitore, Documento documento) {
        this.idPartita = idPartita;
        this.idSessione = idSessione;
        this.offsetIniziale = offsetIniziale;
        this.lunghezza = lunghezza;
        this.shiftCesare = shiftCesare;
        this.parolaSoluzione = parolaSoluzione;
        this.secondiRispostaG1 = secondiRispostaG1;
        this.secondiRispostaG2 = secondiRispostaG2;

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

    public int getOffsetIniziale() {
        return offsetIniziale;
    }

    public void setOffsetIniziale(int offsetIniziale) {
        this.offsetIniziale = offsetIniziale;
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

    public String getDifficolta() {
        return difficolta;
    }

    public void setDifficolta(String difficolta) {
        this.difficolta = difficolta;
    }

    public UtenteEntity getVincitore() {
        return vincitore;
    }

    public void setVincitore(UtenteEntity vincitore) {
        this.vincitore = vincitore;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    
            
}
