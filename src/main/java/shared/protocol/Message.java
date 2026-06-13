package shared.protocol;

import java.io.Serializable;

/**
 * Rappresenta l'unità fondamentale di comunicazione di rete tra Client e Server.
 * Incapsula il tipo di operazione richiesta o la notifica ({@link MessageType})
 * e il relativo carico utile (Payload), che può variare a seconda del contesto.
 */
public class Message implements Serializable {
    private MessageType msgType;
    private Object payload;

    /**
     * Costruisce un nuovo messaggio di rete.
     *
     * @param msgType Il tipo di messaggio che identifica l'azione o l'evento.
     * @param payload I dati associati al messaggio (solitamente un DTO), o {@code null} se non necessari.
     */
    public Message(MessageType msgType, Object payload){
        this.msgType = msgType;
        this.payload = payload;
    }

    /**
     * @return Il tipo del messaggio scambiato.
     */
    public MessageType getMsgType() {
        return msgType;
    }

    /**
     * @return L'oggetto dati associato al messaggio. Dovrà essere sottoposto a cast nel tipo appropriato.
     */
    public Object getPayload() {
        return payload;
    }
}
