package server.model.network.state;

import shared.protocol.MessageType;

import java.lang.annotation.*;

/**
 * Annotazione utilizzata per contrassegnare i metodi all'interno degli stati ({@link ClientState})
 * che hanno il compito di gestire un determinato tipo di messaggio proveniente dalla rete.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageHandler {

    /**
     * Indica a quale {@link MessageType} si riferisce il metodo annotato.
     * @return Il tipo di messaggio (es: {@code MessageType.login}).
     */
    MessageType value(); // così definisco il metodo
}
