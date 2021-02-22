package Networking.Messages;

import Logic.Card;
import Logic.Game;
import Logic.Player;
import Logic.Pot;
import UI.Main;
import com.codebrig.beam.Communicator;
import com.codebrig.beam.messages.BeamMessage;
import com.codebrig.beam.messages.LegacyMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * This message includes all up-to-date game data:
 *      Action index
 *      Bets
 *      Stacks
 *      Pots
 *      Number of pots (side pots)
 *      Dealer, BB, SB indexes
 *      Table cards
 */
public class GameDataMessage extends LegacyMessage {
    public final static int MESSAGE_ID = 1004;

    public final static String ACTION_INDEX = "action";
    public final static String BETS = "bets";
    public final static String STACKS = "stacks";
    public final static String POTS = "pots";
    public final static String NUM_POTS = "numPots";
    public final static String DEALER_INDEX = "dealerIndex";
    public final static String SB_INDEX = "sbIndex";
    public final static String BB_INDEX = "bbIndex";
    public final static String TABLE_CARDS = "tableCards";

    public GameDataMessage(){
        super(MESSAGE_ID);

        // send action index
        setInt(ACTION_INDEX, Game.getCurrentActionIndex());

        // send dealer index
        setInt(DEALER_INDEX, Game.getDealerIndex());

        // send dealer index
        setInt(SB_INDEX, Game.getSmallBlindIndex());

        // send dealer index
        setInt(BB_INDEX, Game.getBigBlindIndex());

        // bets and stacks
        List<Player> players = Game.getPlayers();
        for(int i = 0; i < Game.getNumPlayers(); i ++){
            // player bets and stacks
            setInt(STACKS + i, players.get(i).getStack());
            setInt(BETS + i, players.get(i).getCurrentBet());
        }

        // pots
        ArrayList<Pot> pots = Game.getPots();
        setInt(NUM_POTS, pots.size());
        for(int i = 0; i < pots.size(); i ++){
            setInt(POTS + i, pots.get(i).getPotValue());
        }

        // turn cards
        Card[] tableCards = Main.getGameWindow().getTable().getTableCards();
        for(int i = 0; i < 5; i ++){
            if(tableCards[i] != null) {
                setInt(TABLE_CARDS + (i* 2), tableCards[i].getValue());
                setInt(TABLE_CARDS + (i* 2) + 1, tableCards[i].getSuitValue());
            }else{
                setInt(TABLE_CARDS + (i* 2), -1);
                setInt(TABLE_CARDS + (i* 2) + 1, -1);
            }
        }
    }

    public GameDataMessage(BeamMessage message) {
        super (message);
    }

    public static void clientHandle(Communicator comm, LegacyMessage message){
        while(PlayerDataMessage.processingPlayerData){
            // chill here until the first set of player data has arrived.
            System.out.println("Waiting....");
            try {
                Thread.sleep(500);
            }catch (InterruptedException e){
                // Fine, bitch. Interrupt me. See if I care....
            }
        }

        // indexes
        Game.setBigBlindIndex(message.getInt(BB_INDEX));
        Game.setSmallBlindIndex(message.getInt(SB_INDEX));
        Game.setDealerIndex(message.getInt(DEALER_INDEX));
        Game.setActionIndex(message.getInt(ACTION_INDEX));

        List<Player> players = Game.getPlayers();
        for(int i = 0; i < Game.getNumPlayers(); i ++){
            // player bets and stacks
            players.get(i).setStack(message.getInt(STACKS + i));
            players.get(i).setCurrentBet(message.getInt(BETS + i));
        }

        // pots
        ArrayList<Pot> pots = new ArrayList<>();
        int numPots = message.getInt(NUM_POTS);
        for(int i = 0; i < numPots; i ++){
            pots.add(new Pot(i, message.getInt(POTS + i)));
        }
        Game.setPots(pots);

        if(!Main.getGameWindow().getIsGameStarted()){
            Main.getGameWindow().setIsGameStarted(true);
        }

        // table cards
        int cardValue, suitValue;
        Card[] cards = new Card[5];
        for(int i = 0; i < 5; i++){
            cardValue = message.getInt(TABLE_CARDS + (i * 2));
            suitValue = message.getInt(TABLE_CARDS + (i * 2) + 1);
            if(cardValue >= 0){
                cards[i] = new Card(cardValue, suitValue);
            }else{
                cards[i] = null;
            }
        }
        Main.getGameWindow().getTable().setTableCards(cards);
    }
}