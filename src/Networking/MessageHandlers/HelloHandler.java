package Networking.MessageHandlers;

import com.codebrig.beam.Communicator;
import com.codebrig.beam.handlers.LegacyHandler;
import com.codebrig.beam.messages.LegacyMessage;

public class HelloHandler extends LegacyHandler {

    public HelloHandler(){
        super(HelloMessage.EXAMPLE_MESSAGE_ID);

    }

    @Override
    public LegacyMessage messageReceived(Communicator communicator, LegacyMessage message) {
        System.out.println ("Client sent message: " + message.getString ("client_message"));

        //response message
        return message.emptyResponse ().setString ("server_response", "example_reply_message");
    }
}
