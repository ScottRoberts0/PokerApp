package Networking.MessageHandlers;

import Networking.Messages.PlayerDataMessage;
import Networking.Messages.PlayersInLobbyMessage;
import Networking.Messages.PlayerNumMessage;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.handlers.LegacyHandler;
import com.codebrig.beam.messages.LegacyMessage;

public class ClientHandler extends LegacyHandler {

    public ClientHandler(){
        super(PlayerDataMessage.MESSAGE_ID, PlayersInLobbyMessage.MESSAGE_ID, PlayerNumMessage.MESSAGE_ID);
    }

    @Override
    public LegacyMessage messageReceived(Communicator comm, LegacyMessage message) {
        // get a response ready
        LegacyMessage response = new LegacyMessage();

        // check the message type
        long messageTypee = message.getType();

        if(messageTypee == PlayerDataMessage.MESSAGE_ID){
            System.out.println("MessageType: Player Data");
            PlayerDataMessage.clientHandle(comm, message);
        }else if(messageTypee == PlayersInLobbyMessage.MESSAGE_ID){
            System.out.println("MessageType: Player Lobby List");
            PlayersInLobbyMessage.clientHandle(comm, message);
        }else if(messageTypee == PlayerNumMessage.MESSAGE_ID){
            System.out.println("MessageType: Start Game");
            PlayerNumMessage.cliendHandle(comm, message);
        }

        return response;
    }
}
