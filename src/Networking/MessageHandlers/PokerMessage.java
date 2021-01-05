package Networking.MessageHandlers;

import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

public class PokerMessage extends LegacyMessage {
    public final static int EXAMPLE_MESSAGE_ID = 1000;

    public final static String MESSAGE_BASIC = "client_message";
    public final static String MESSAGE_HANDSHAKE = "handshake";
    public final static String MESSAGE_PLAYERNUM = "playernum";

    public PokerMessage() {
        super(EXAMPLE_MESSAGE_ID);
    }

    public PokerMessage(BeamMessage message) {
        super (message);
    }

}
