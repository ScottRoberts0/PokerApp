package Networking.Messages;

import Networking.Networker;
import UI.Main;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

/**
 * This message data is a notification that a hand has ended and includes who won with what hand
 */
public class EndHandMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1006;

    public final static String WINNER_COUNT = "winnerCount";
    public final static String WINNER_TEXT = "winnerText";

    public EndHandMessage() {
        super(MESSAGE_ID);
    }

    public EndHandMessage(String... winningText){
        super(MESSAGE_ID);

        setInt(WINNER_COUNT, winningText.length);

        for(int i = 0; i < winningText.length; i ++){
            setString(WINNER_TEXT + i, winningText[i]);
        }
    }

    public EndHandMessage(BeamMessage message) {
        super (message);
    }

    public static void clientHandle(Communicator communicator, LegacyMessage message){
        // wipe out player and table cards
        Main.getGameWindow().getTable().deletePlayerCards();

        // reset table cards
        Main.getGameWindow().getTable().resetTableCardsAnimated();
    }
}