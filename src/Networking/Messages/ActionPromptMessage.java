package Networking.Messages;

import Logic.Game;
import Logic.Player;
import Networking.Networker;
import UI.Main;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

import java.util.ArrayList;

/**
 * This message data includes what action(s) a client is able to take and what the max/min bets are to be
 */
public class ActionPromptMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1005;

    public static final int CHECK_BIT = 1; //4'b0001
    public static final int FOLD_BIT = 2;  //4'b0010
    public static final int CALL_BIT = 4;  //4'b0100
    public static final int RAISE_BIT = 8; //4'b1000

    private static final String BUTTONS_ALLOWED = "buttonsAllowed";
    private static final String MIN_BET = "minBet";
    private static final String MAX_BET = "maxBet";

    public ActionPromptMessage(){
        super(MESSAGE_ID);

        // send which buttons can be pressed by the client
        int buttonsAllowed = 0;

        if(Game.checkCheckAllowed()) buttonsAllowed |= CHECK_BIT;
        if(Game.checkFoldAllowed()) buttonsAllowed |= FOLD_BIT;
        if(Game.checkCallAllowed()) buttonsAllowed |= CALL_BIT;
        if(Game.checkRaiseAllowed()) buttonsAllowed |= RAISE_BIT;

        setInt(BUTTONS_ALLOWED, buttonsAllowed);

        // set minimum bet amount
        int minBet;
        if (Game.getStreet() != 0 && Game.getHighestBet() == 0) {
            minBet = Game.getMinBet();
        } else {
            minBet = Game.getLastRaiseSize() + Game.getHighestBet();
        }
        setInt(MIN_BET, minBet);

        // set max bet amount
        int actionIndex = Game.getCurrentActionIndex();
        ArrayList<Player> players = Game.getPlayers();

        int maxBet = players.get(actionIndex).getStack() +
                Game.getCurrentPot().getBets()[players.get(actionIndex).getPlayerNum()];

        setInt(MAX_BET, maxBet);
        System.out.println("Max/Min: " + maxBet + "/" + minBet);
    }

    public ActionPromptMessage(BeamMessage message) {
        super (message);
    }

    public static void clientHandle(Communicator communicator, LegacyMessage message){
        // grab the things
        int actionsAllowed = message.getInt(BUTTONS_ALLOWED);
        int minBet = message.getInt(MIN_BET);
        int maxBet = message.getInt(MAX_BET);

        // enable the buttons
        Main.getGameWindow().updateButtons(actionsAllowed, minBet, maxBet);
    }
}