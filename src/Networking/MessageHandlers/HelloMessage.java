package Networking.MessageHandlers;

import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

public class HelloMessage extends LegacyMessage {
    public final static int EXAMPLE_MESSAGE_ID = 1000;

    public HelloMessage () {
        super(EXAMPLE_MESSAGE_ID);
    }

    public HelloMessage (BeamMessage message) {
        super (message);
    }

}
