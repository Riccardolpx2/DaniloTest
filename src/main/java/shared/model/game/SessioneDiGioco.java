/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.model.game;

import java.util.ArrayList;
import java.util.List;
import shared.model.Utente;

/**
 *
 * @author Utente
 */
public class SessioneDiGioco {
    private int idSessione;
    private int durataSessione;
    private List<Partita> partite;
    private Utente player1;
    private Utente player2;

    public SessioneDiGioco(int idSessione, int durataSessione, Utente player1, Utente player2) {
        this.idSessione = idSessione;
        this.durataSessione = durataSessione;
        this.partite = new ArrayList<>();
        this.player1 = player1;
        this.player2 = player2;
    }

    public int getIdSessione() {
        return idSessione;
    }

    public void setIdSessione(int idSessione) {
        this.idSessione = idSessione;
    }

    public int getDurataSessione() {
        return durataSessione;
    }

    public void setDurataSessione(int durataSessione) {
        this.durataSessione = durataSessione;
    }

    public List<Partita> getPartita() {
        return partite;
    }

    public void setPartita(List<Partita> partita) {
        this.partite = partita;
    }

    public Utente getPlayer1() {
        return player1;
    }

    public void setPlayer1(Utente player1) {
        this.player1 = player1;
    }

    public Utente getPlayer2() {
        return player2;
    }

    public void setPlayer2(Utente player2) {
        this.player2 = player2;
    }

   
    
    
}
