package Networking.MessageHandlers;

import Networking.Messages.PokerClientMessage;
import Networking.Messages.PokerMessage;
import Networking.Messages.PokerServerMessage;
import Networking.Networker;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.handlers.LegacyHandler;
import com.codebrig.beam.messages.LegacyMessage;

public class PokerHandler extends LegacyHandler {

    public static String SERVER_RESPONSE = "server_response";

    public PokerHandler(){
        super(PokerClientMessage.EXAMPLE_MESSAGE_ID);
    }

    @Override
    public LegacyMessage messageReceived(Communicator communicator, LegacyMessage message) {
        // handle the received message given the user type
        if(Networker.getInstance().getIsServer())
            return serverMessageReceived(communicator, message);
        else
            return clientMessageReceived(communicator, message);
    }

    private LegacyMessage serverMessageReceived(Communicator communicator, LegacyMessage message){
        // get a response ready
        PokerServerMessage response;

        // get the message type
        String messageType = message.getString(PokerMessage.MESSAGE_TYPE);
        if (messageType != null) {
            System.out.println ("Type: " + messageType);

            // ------------------------ Handshaking -----------------------
            if(messageType.equals(PokerMessage.MESSAGE_TYPE_HANDSHAKE)){
                // handle handshaking
                response = PokerServerMessage.handleHandshakingMessage(message);
            }
            // ------------------------ Unrecognized Message Type Error -----------------------
            else {
                // no type recognized, send back some error stuffs
                response = PokerServerMessage.handleUnknownMessageType(message);
            }
        }
        // ------------------------ No message type -----------------------
        else{
            response = PokerServerMessage.handleNoMessageType(message);
        }

        return response;
    }

    private LegacyMessage clientMessageReceived(Communicator communicator, LegacyMessage message){
        // get a response ready
        PokerServerMessage response = new PokerServerMessage();

        String messageType = message.getString(PokerMessage.MESSAGE_TYPE);
        // ------------------------ Game Data Message -----------------------
        if(messageType.equals(PokerMessage.MESSAGE_TYPE_GAME_DATA)){
            PokerClientMessage.handleGameDataMessage(message);
        }

        return response;
    }

}
