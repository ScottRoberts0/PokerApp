package Networking.Messages;

import Logic.Game;
import UI.LobbyWindow;
import UI.Main;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

public class PlayerNumMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1003;

    public PlayerNumMessage() {
        super(MESSAGE_ID);
    }

    public PlayerNumMessage(int playerNum){
        super(MESSAGE_ID);
        this.setInt(PokerMessage.MESSAGE_PLAYERNUM, playerNum);
    }

    public PlayerNumMessage(BeamMessage message) {
        super (message);
    }

    public static void serverHandle(Communicator communicator, LegacyMessage message){
    }

    public static void cliendHandle(Communicator communicator, LegacyMessage message){
        // grab the player number
        int playerNum = message.getInt(PokerMessage.MESSAGE_PLAYERNUM);

        System.out.println("Player Number: " + playerNum);

        Game.setLocalPlayerNum(playerNum);

        // open some blank table GUI
        Main.beginGameUI();

        LobbyWindow.close();
    }
}

