package Networking.MessageHandlers;

import Networking.Messages.ClientConnectedMessage;
import Networking.Messages.PlayerDataMessage;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.handlers.LegacyHandler;
import com.codebrig.beam.messages.LegacyMessage;

public class ClientHandler extends LegacyHandler {

    public static String SERVER_RESPONSE = "server_response";

    public ClientHandler(){
        super(PlayerDataMessage.MESSAGE_ID);
    }

    @Override
    public LegacyMessage messageReceived(Communicator comm, LegacyMessage message) {
        // get a response ready
        LegacyMessage response = null;

        // check the message type
        long messageTypee = message.getType();

        if(messageTypee == PlayerDataMessage.MESSAGE_ID){
            System.out.println("MessageType: Player Data");
            PlayerDataMessage.clientReceivedPlayerDataBroadcast(comm, message);
        }

        return response;
    }
}
