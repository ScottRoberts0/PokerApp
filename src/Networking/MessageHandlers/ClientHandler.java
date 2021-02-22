package Networking.MessageHandlers;

import Networking.Messages.*;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.handlers.LegacyHandler;
import com.codebrig.beam.messages.LegacyMessage;

public class ClientHandler extends LegacyHandler {

    public ClientHandler(){
        super(  PlayerDataMessage.MESSAGE_ID, PlayersInLobbyMessage.MESSAGE_ID,
                StartGameMessage.MESSAGE_ID, GameDataMessage.MESSAGE_ID,
                ActionPromptMessage.MESSAGE_ID, EndHandMessage.MESSAGE_ID);
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
        }else if(messageTypee == StartGameMessage.MESSAGE_ID){
            System.out.println("MessageType: Start Game");
            StartGameMessage.clientHandle(comm, message);
        }else if(messageTypee == GameDataMessage.MESSAGE_ID){
            System.out.println("MessageType: Game data");
            GameDataMessage.clientHandle(comm, message);
        }else if(messageTypee == ActionPromptMessage.MESSAGE_ID){
            System.out.println("MessageType: Action Prompt");
            ActionPromptMessage.clientHandle(comm, message);
        }else if(messageTypee == EndHandMessage.MESSAGE_ID){
            System.out.println("MessageType: End Hand");
            EndHandMessage.clientHandle(comm, message);
        }

        return response;
    }
}
