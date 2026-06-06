package server.model.network.state;

import shared.model.protocol.MessageType;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageHandler {
    MessageType value(); // così definisco il metodo
}
