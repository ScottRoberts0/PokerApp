package Networking.Messages;

import Logic.Game;
import Logic.Player;
import Networking.Networker;
import UI.Main;
import UI.MainWindow;
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
        int playerNum;
        if(Game.getPlayers() == null){
            playerNum = 0;
        }else{
            playerNum = Game.getPlayers().size();
        }
        response.setInt(PokerMessage.MESSAGE_PLAYERNUM, playerNum);

        // create a player with their player name
        Game.addPlayer(new Player(playerNum, 100000, playerName));

        // temp
        Main.getGameWindow().setPlayerNumOnButton();

        return response;
    }

    public static PokerServerMessage handleUnknownMessageType(LegacyMessage message){
        PokerServerMessage response = new PokerServerMessage();

        response.setString(PokerMessage.MESSAGE_TYPE, PokerMessage.MESSAGE_TYPE_ERROR);
        response.setString(PokerMessage.MESSAGE_ERROR, "Did not recognize message type, bitch.");

        return response;
    }

    public static PokerServerMessage handleNoMessageType(LegacyMessage message){
        PokerServerMessage response = new PokerServerMessage();


        response.setString(PokerMessage.MESSAGE_TYPE, PokerMessage.MESSAGE_TYPE_ERROR);
        response.setString(PokerMessage.MESSAGE_ERROR, "No message type. Did we lose it?");

        return response;
    }
}
