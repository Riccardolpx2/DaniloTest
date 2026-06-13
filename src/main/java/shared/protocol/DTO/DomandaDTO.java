/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.protocol.DTO;

import server.gameUtil.Domanda;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object (DTO) che rappresenta la domanda di un singolo round di gioco.
 * Viene inviato dal Server ai Client. Contiene il testo della sfida in cui sono
 * state nascoste delle parole e la lista delle rispettive versioni cifrate.
 */
public class DomandaDTO implements Serializable{
    
    private String testoCifrato;
    private List<String> paroleCifrate;

    /**
     * Costruisce il DTO a partire dai dati espliciti.
     *
     * @param testoCifrato Il testo in cui la/le parola/e da trovare sono cifrate (offuscate).
     * @param paroleCifrate La lista delle parole modificate dal cifrario.
     */
    public DomandaDTO(String testoCifrato, List<String> paroleCifrate) {
        this.testoCifrato = testoCifrato;
        this.paroleCifrate=paroleCifrate;
    }

    /**
     * Costruisce il DTO estrapolando i dati dall'oggetto logico {@link Domanda}.
     * @param domanda L'oggetto di dominio contenente le specifiche del round.
     */
    public DomandaDTO(Domanda domanda){
        this(domanda.getTestoCifrato(), domanda.getParoleSoluzioniCifrate());
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
