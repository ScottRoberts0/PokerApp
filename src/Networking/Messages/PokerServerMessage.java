package Networking.Messages;

import Networking.Networker;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

public class PokerServerMessage extends LegacyMessage {
    public final static int EXAMPLE_MESSAGE_ID = 1000;

    public PokerServerMessage() {
        super(EXAMPLE_MESSAGE_ID);
    }

    public PokerServerMessage(BeamMessage message) {
        super (message);
    }

    public static PokerServerMessage handleHandshakingMessage(LegacyMessage message){
        PokerServerMessage response = new PokerServerMessage();

        // grab the playername
        String playerName = message.get(PokerMessage.MESSAGE_PLAYERNAME);
        System.out.println(playerName + "");

        // set the response type
        response.setString(PokerMessage.MESSAGE_TYPE, PokerMessage.MESSAGE_TYPE_HANDSHAKE);
        // send the player back their playerNum
        response.setInt(PokerMessage.MESSAGE_PLAYERNUM, Networker.getInstance().getNumPlayers());


        // increment the players
        Networker.getInstance().incrementPlayers();

        return response;
    }
}
