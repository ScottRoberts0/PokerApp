package Networking.Messages;

import Networking.Messages.PokerMessage;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

public class PokerClientMessage extends LegacyMessage {
    public final static int EXAMPLE_MESSAGE_ID = 1000;
    public PokerClientMessage() {
        super(EXAMPLE_MESSAGE_ID);
    }

    public PokerClientMessage(BeamMessage message) {
        super (message);
    }

    public static void handleClientMessage(LegacyMessage message){
        String messageType = message.getString(PokerMessage.MESSAGE_TYPE);
        System.out.println("Type: " + messageType);

        // ---------------------------- Handshake -----------------------
        if(messageType.equals(PokerMessage.MESSAGE_TYPE_HANDSHAKE)){
            Integer playerNum = message.getInt(PokerMessage.MESSAGE_PLAYERNUM);

            System.out.println("PlayerNum: " + playerNum);
        }
        // ---------------------------- Error -----------------------
        else if(messageType.equals(PokerMessage.MESSAGE_TYPE_ERROR)){

        }


    }
}