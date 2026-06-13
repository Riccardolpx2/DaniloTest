/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.gameUtil;


/**
 * Rappresenta un documento di testo presente nel sistema.
 * Contiene il materiale testuale in chiaro (es. un articolo, una frase o un brano)
 * utilizzato come base per la generazione degli enigmi o delle domande cifrate.
 * @author Utente
 */
public class Documento {
    private int idDocumento;
    private String nome;
    private String testo;
    
    /**
     * Costruttore completo per l'inizializzazione di un oggetto Documento.
     * Viene utilizzato tipicamente dal DAO per caricare i testi originali dal Database.
     * @param idDocumento L'identificativo univoco del documento nel database.
     * @param nome Il titolo o una breve descrizione identificativa del documento.
     * @param testo Il contenuto testuale completo in chiaro.
     */
    public Documento(int idDocumento, String nome, String testo) {
        this.idDocumento = idDocumento;
        this.nome = nome;
        this.testo = testo;
    }
    

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
   
}
