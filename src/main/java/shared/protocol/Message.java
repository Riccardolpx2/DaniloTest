package shared.protocol;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType msgType;
    private Object payload;


    public Message(MessageType msgType, Object payload){
        this.msgType = msgType;
        this.payload = payload;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public Object getPayload() {
        return payload;
    }


    // TODO: Da continuare con la struttura
}
