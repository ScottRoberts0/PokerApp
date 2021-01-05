package Networking.MessageHandlers;

import com.codebrig.beam.Communicator;
import com.codebrig.beam.handlers.LegacyHandler;
import com.codebrig.beam.messages.LegacyMessage;

public class PokerHandler extends LegacyHandler {

    public static String SERVER_RESPONSE = "server_response";

    public PokerHandler(){
        super(PokerMessage.EXAMPLE_MESSAGE_ID);
    }

    @Override
    public LegacyMessage messageReceived(Communicator communicator, LegacyMessage message) {
        String Clientmessage = message.getString(PokerMessage.MESSAGE_BASIC);
        System.out.println ("Client sent message: " + Clientmessage);

        //response message
        return message.emptyResponse().setString (SERVER_RESPONSE, "Received: " + Clientmessage);
    }
}
