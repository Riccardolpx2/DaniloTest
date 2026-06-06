package shared.protocol;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType msgType;
    private String playerID; //username
    private Object payload;

    // Questo viene utilizzato dal client
    public Message(MessageType msgType, String playerID, Object payload) {
        this.msgType = msgType;
        this.playerID = playerID;
        this.payload = payload;
    }

    // Questo viene utilizzato dal server
    public Message(MessageType msgType, Object payload){
        this.msgType = msgType;
        this.payload = payload;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public String getPlayerID() {
        return playerID;
    }

    public Object getPayload() {
        return payload;
    }


    // TODO: Da continuare con la struttura
}
