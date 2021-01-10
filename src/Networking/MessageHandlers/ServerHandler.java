package Networking.MessageHandlers;

import Networking.Messages.*;
import Networking.Networker;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.handlers.LegacyHandler;
import com.codebrig.beam.messages.LegacyMessage;

public class ServerHandler extends LegacyHandler {

    public static String SERVER_RESPONSE = "server_response";

    public ServerHandler(){
        super(ClientConnectedMessage.MESSAGE_ID);
    }

    @Override
    public LegacyMessage messageReceived(Communicator communicator, LegacyMessage message) {
        // get a response ready
        LegacyMessage response = null;

        // check the message type
        long messageTypee = message.getType();

        if(messageTypee == ClientConnectedMessage.MESSAGE_ID){
            System.out.println("MessageType: Client Connected");
            response = ClientConnectedMessage.serverClientConnected(communicator, message);
        }

        return response;
    }

}
