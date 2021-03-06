package Networking.Messages;

import Logic.Card;
import Logic.Game;
import Logic.Player;
import Networking.Networker;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * This message data includes the names of all players currently connected
 */
public class PlayersInLobbyMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1002;

    public final static String MESSAGE_NUMPLAYERS = "numplayers";
    public final static String MESSAGE_PLAYER_STRING = "ps";

    public PlayersInLobbyMessage() {
        super(MESSAGE_ID);

        // grab the players
        ArrayList<String> playerList = Networker.getInstance().getPlayersInLobby();

        // start plugging this message full of data
        // add number of players
        this.setInt(MESSAGE_NUMPLAYERS, playerList.size());

        // add player data
        for(int i = 0; i < playerList.size(); i ++){
            this.setString(MESSAGE_PLAYER_STRING + i, playerList.get(i));
        }
    }

    public PlayersInLobbyMessage(BeamMessage message) {
        super (message);
    }

    public static void clientHandle(Communicator communicator, LegacyMessage message){
        // this message is always a broadcast, no need for a response

        // delete all previous players in the player list
        Networker.getInstance().clearPlayerList();

        int numPlayers = message.getInt(MESSAGE_NUMPLAYERS);

        // get player data
        for(int i = 0; i < numPlayers; i ++){
            String name = message.getString(MESSAGE_PLAYER_STRING + i);

            Networker.getInstance().addPlayerToLobby(name);
        }
    }
}
