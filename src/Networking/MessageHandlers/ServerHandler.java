package Networking.MessageHandlers;

import Networking.Messages.*;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.handlers.LegacyHandler;
import com.codebrig.beam.messages.LegacyMessage;

public class ServerHandler extends LegacyHandler {

    public ServerHandler(){
        super(ClientConnectedMessage.MESSAGE_ID, PlayerNumMessage.MESSAGE_ID);
    }

    @Override
    public LegacyMessage messageReceived(Communicator communicator, LegacyMessage message) {
        // get a response ready
        LegacyMessage response = new LegacyMessage();

        // check the message type
        long messageTypee = message.getType();

        if(messageTypee == ClientConnectedMessage.MESSAGE_ID){
            ClientConnectedMessage.serverHandle(communicator, message);
        }

        return response;
    }

}
