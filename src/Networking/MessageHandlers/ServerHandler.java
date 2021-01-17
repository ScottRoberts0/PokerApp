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
        LegacyMessage response = new LegacyMessage();

        // check the message type
        long messageTypee = message.getType();

        if(messageTypee == ClientConnectedMessage.MESSAGE_ID){
            handleClientConnected(communicator, message);
        }

        return response;
    }

    private void handleClientConnected(Communicator communicator, LegacyMessage message){
        System.out.println("MessageType: Client Connected");
        // grab the playername
        String playerName = message.get(PokerMessage.MESSAGE_PLAYERNAME);
        System.out.println(playerName + "");

        // add to the players in lobby list
        Networker.getInstance().addPlayerToLobby(playerName);

        // send a broadcast back to all players with new lobby list
        Networker.getInstance().broadcastLobbyPlayerList();
    }

}
