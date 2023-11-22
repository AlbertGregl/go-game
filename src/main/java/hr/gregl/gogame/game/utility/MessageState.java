package hr.gregl.gogame.game.utility;

import java.io.Serial;
import java.io.Serializable;

public class MessageState implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private final String message;
    private final String sender;

    public MessageState(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "MessageState{" +
                "message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }

}
