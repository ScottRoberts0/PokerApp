package Networking.Messages;

import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

public class PokerServerMessage extends LegacyMessage {
    public final static int EXAMPLE_MESSAGE_ID = 1000;

    public PokerServerMessage() {
        super(EXAMPLE_MESSAGE_ID);
    }

    public PokerServerMessage(BeamMessage message) {
        super (message);
    }

}
