package Networking.Messages;

import Logic.Card;
import Logic.Game;
import Logic.Player;
import UI.Main;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

import java.util.List;

public class PlayerDataMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1001;

    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_CARD1_VALUE = "value0";
    public static final String PLAYER_CARD1_SUIT = "suit0";
    public static final String PLAYER_CARD2_VALUE = "value1";
    public static final String PLAYER_CARD2_SUIT = "suit1";
    public static final String PLAYER_STACK = "stack";

    public static boolean waitingForFirstPlayerData = true;

    public PlayerDataMessage() {
        super(MESSAGE_ID);

        // grab the players
        List<Player> players = Game.getPlayers();

        // start plugging this message full of data
        // add number of players
        this.setInt(PokerMessage.MESSAGE_NUMPLAYERS, players.size());

        // add player data
        for(int i = 0; i < players.size(); i ++){
            // playername
            setString(PLAYER_NAME + i, players.get(i).getPlayerName());

            // player cards, or -1 if the card is null
            Card[] hand = players.get(i).getHand();
            if(hand[0] != null){
                setInt(PLAYER_CARD1_VALUE + i, hand[0].getValue());
                setInt(PLAYER_CARD1_SUIT + i, hand[0].getSuitValue());
            }else{
                setInt(PLAYER_CARD1_VALUE + i, -1);
                setInt(PLAYER_CARD1_SUIT + i, -1);
            }
            if(hand[1] != null){
                setInt(PLAYER_CARD2_VALUE + i, hand[1].getValue());
                setInt(PLAYER_CARD2_SUIT + i, hand[1].getSuitValue());
            }else{
                setInt(PLAYER_CARD2_VALUE + i, -1);
                setInt(PLAYER_CARD2_SUIT + i, -1);
            }

            // stack
            setInt(PLAYER_STACK + i, players.get(i).getStack());
        }
    }

    public PlayerDataMessage(BeamMessage message) {
        super (message);
    }

    public static void clientHandle(Communicator communicator, LegacyMessage message){
        // attach the number of players
        Integer numPlayers = message.getInt(PokerMessage.MESSAGE_NUMPLAYERS);
        System.out.println("Num Players: " + numPlayers);

        // cycle through the players in the message
        for(int i = 0; i < numPlayers; i++){
            String playerName = message.getString(PLAYER_NAME + i);
            int card0Value, card0Suit, card1Value, card1Suit, stack;

            card0Value = message.getInt(PLAYER_CARD1_VALUE + i);

            card0Suit = message.getInt(PLAYER_CARD1_SUIT + i);

            card1Value = message.getInt(PLAYER_CARD2_VALUE + i);

            card1Suit = message.getInt(PLAYER_CARD2_SUIT + i);

            stack = message.getInt(PLAYER_STACK + i);

            Player player = new Player(i, stack, playerName);
            if(card0Suit == -1){
                player.setHand(new Card[] { null, null});
            }else{
                player.setHand(new Card[] { new Card(card0Value, card0Suit), new Card(card1Value, card1Suit)});
            }

            System.out.println(player);

            Game.addPlayer(player);
        }

        Main.getGameWindow().getTable().createPlayerCards(true);

        System.out.println("Done player data");

        waitingForFirstPlayerData = false;
    }
}
