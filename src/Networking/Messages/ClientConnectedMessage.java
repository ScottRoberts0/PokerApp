package Networking.Messages;

import Logic.Game;
import Logic.Player;
import Networking.Networker;
import UI.Main;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;
import com.sun.jdi.connect.Connector;

public class ClientConnectedMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1000;

    public ClientConnectedMessage() {
        super(MESSAGE_ID);
    }

    public ClientConnectedMessage(String playerName){
        super(MESSAGE_ID);
        this.setString(PokerMessage.MESSAGE_PLAYERNAME, playerName);
    }

    public ClientConnectedMessage(BeamMessage message) {
        super (message);
    }

    public static void serverHandle(Communicator communicator, LegacyMessage message){
        System.out.println("MessageType: Client Connected");
        // grab the playername
        String playerName = message.get(PokerMessage.MESSAGE_PLAYERNAME);
        System.out.println(playerName + "");

        // add to the players in lobby list
        Networker.getInstance().addPlayerToLobby(playerName, communicator.getUID());
        System.out.println(communicator.getUID() + "");

        // send a broadcast back to all players with new lobby list
        Networker.getInstance().broadcastLobbyPlayerList();
    }
}