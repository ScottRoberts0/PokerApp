package Networking.MessageHandlers;

import Networking.Messages.*;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.handlers.LegacyHandler;
import com.codebrig.beam.messages.LegacyMessage;

public class ServerHandler extends LegacyHandler {

    public ServerHandler(){
        super(  ClientConnectedMessage.MESSAGE_ID, StartGameMessage.MESSAGE_ID,
                ClientActionMessage.MESSAGE_ID);
    }

    @Override
    public LegacyMessage messageReceived(Communicator communicator, LegacyMessage message) {
        // get a response ready
        LegacyMessage response = new LegacyMessage();

        // check the message type
        long messageTypee = message.getType();

        if(messageTypee == ClientConnectedMessage.MESSAGE_ID){
            System.out.println("MessageType: Client Connected");
            ClientConnectedMessage.serverHandle(communicator, message);
        }else if(messageTypee == ClientActionMessage.MESSAGE_ID){
            System.out.println("MessageType: Client Action");
            ClientActionMessage.serverHandle(communicator, message);
        }

        return response;
    }

}
