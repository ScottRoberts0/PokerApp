package Logic;

import java.util.ArrayList;
import java.util.Arrays;

public class Player implements Comparable {

    private final int playerNum;
    private String playerName;

    private int currentBet; //value used by the UI to display player's current bet
    private int stack;
    private int streetStartingStackSize;
    private boolean hasFolded;

    private Card[] hand; //player's hole cards
    private Card[] possCards; //array used by evaluator class to create a made hand
    private Card[] madeHand; //the player's best possible hand after all cards are dealt

    public Player(int playerNum) {
        hand = new Card[2];
        possCards = new Card[7];
        madeHand = new Card[5];
        this.playerNum = playerNum;
    }

    public Player(int playerNum, int stack) {
        hand = new Card[2];
        possCards = new Card[7];
        madeHand = new Card[5];
        this.playerNum = playerNum;
        this.stack = stack;
        this.streetStartingStackSize = this.stack;
        this.playerName = "Player " + playerNum;
    }

    public Player(int playerNum, int stack, String playerName) {
        hand = new Card[2];
        possCards = new Card[7];
        madeHand = new Card[5];
        this.playerNum = playerNum;
        this.stack = stack;
        this.streetStartingStackSize = this.stack;
        this.playerName = playerName;
    }

    public void setHand(Card[] cards){
        this.hand = cards;
    }


    public void resetHand() {
        Arrays.fill(hand, null);
    }

    public void refundBet(int bet, Pot pot) {
        stack += bet;
        pot.removeFromPot(bet);
        currentBet -= bet;
    }

    /**
     * @param betSize blind value
     * @param mainPot blinds should only ever be put into the main pot
     */
    public void postBlind(int betSize, Pot mainPot) {
        if(stack - betSize < 0) {
            betSize = stack;
        }

        stack -= betSize;
        mainPot.addToPot(betSize, playerNum);
        mainPot.addPlayerToPot(this);
        currentBet = betSize;

        Game.tryWriteActionToHH(playerName + " posts blind " + betSize);
    }

    public void setStack(int stack){
        this.stack = stack;
    }

    public void setCurrentBet(int bet){
        this.currentBet = bet;
    }

    public void raise(int betSize, Pot pot) {
        //bet sizes are formatted by Game class before being passed in
        //bet sizes are formatted to be exactly the value passed in by the player
        if(stack - betSize == 0) {
            //if the player is going all in
            currentBet += betSize;
            stack -= betSize;
            pot.addToPot(betSize, playerNum);

            //if the current pot does not yet have this player, add to the current pot (case: preflop only)
            if(!pot.containsPlayer(this)) {
                pot.addPlayerToPot(this);
            }
        } else {
            //if the player is not going all in
            //since we are raising to exactly what the player passes in, we take out only the difference between the raise size, and what they have already put in
            stack -= betSize - pot.getBets()[playerNum];
            pot.addToPot(betSize - pot.getBets()[playerNum], playerNum);

            //if the current pot does not yet have this player, add to the current pot (case: preflop only)
            if(!pot.containsPlayer(this)) {
                pot.addPlayerToPot(this);
            }

            currentBet = betSize;
        }

        pot.setPlayerActed(playerNum, true);

        String output = playerName + " (" + stack + ")" + " raises to " + pot.getBets()[playerNum];
        Game.tryWriteActionToHH(output);

        System.out.println(output);
        System.out.println();
    }

    public void call(Pot pot) {
        //formats a the callsize by taking the difference between the highest bet and the current highest bet, the value to call to
        int callSize = Game.getHighestBet() - pot.getBets()[playerNum];

        //doesn't allow a player to go below 0 stack
        if(stack - callSize < 0) {
            callSize = stack;
        }

        currentBet += callSize;

        stack -= callSize;
        pot.addToPot(callSize, playerNum);

        //adds the player to the current pot if it is not already included
        if(!pot.containsPlayer(this)) {
            pot.addPlayerToPot(this);
        }

        pot.setPlayerActed(playerNum, true);

        String output = playerName + " (" + stack + ")"  + " calls " + callSize;
        Game.tryWriteActionToHH(output);

        System.out.println(output);
        System.out.println();
    }

    public void fold(ArrayList<Pot> pots) {
        currentBet = 0;
        resetHand();

        //when a player folds, they should be removed from every pot
        for(Pot pot : pots) {
            pot.removePlayerFromPot(this);
            pot.setPlayerActed(playerNum, false);
        }

        this.hasFolded = true;

        String output = playerName + " (" + stack + ")"  + " folds";
        Game.tryWriteActionToHH(output);

        System.out.println(output);
        System.out.println();
    }

    public void check(Pot pot) {
        //basically does nothing except they have acted
        pot.setPlayerActed(playerNum, true);

        String output = playerName + " (" + stack + ")"  + " checks";
        Game.tryWriteActionToHH(output);

        System.out.println(output);
        System.out.println();
    }

    public String win(int potSize) {
        String output;
        stack += potSize;
        if(madeHand[0] == null || Game.getCurrentPot().getNumPlayersInPot() == 1) {
            output = playerName + " wins " + potSize + " satoshis!";
        } else {
            output = playerName + " wins " + potSize + " satoshis with a " + getMadeHandName() + "!";
        }

        Game.tryWriteActionToHH(output);
        System.out.println(output);
        System.out.println();

        return output;
    }

    public void resetMoneyInPot() {
        currentBet = 0;
    }

    public void resetFolded() {
        this.hasFolded = false;
    }

    public void resetStack() {
        this.stack = Game.getStartingStackSize();
    }

    public void updateStreetStartingStackSize() {
        this.streetStartingStackSize = this.stack;
    }

    /**
     * This method draws a random hand.
     * @param deck The deck being used.
     */
    public void drawHand(Deck deck) {
        hand[0] = deck.drawCard();
        hand[1] = deck.drawCard();
    }

    /**
     * This method allows you to draw a specific hand input by the user.
     * @param deck The deck being used.
     * @param value1 The value (1 [Ace] - 13 [King]) of the first card.
     * @param suit1 The suit value (0 = Diamonds, 1 = Hearts, 2 = Spades, 3 = Clubs) of the first card.
     * @param value2 The value (1 [Ace] - 13 [King]) of the second card.
     * @param suit2 The suit value (0 = Diamonds, 1 = Hearts, 2 = Spades, 3 = Clubs) of the second card.
     */
    public void drawHand(Deck deck, int value1, int suit1, int value2, int suit2) throws IllegalArgumentException {
        if(value1 > 13 || value1 < 1 || value2 > 13 || value2 < 1 || suit1 > 3 || suit1 < 0 || suit2 > 3 || suit2 < 0) {
            throw new IllegalArgumentException("Invalid argument for Card");
        }

        hand[0] = deck.drawCard(value1, suit1);
        hand[1] = deck.drawCard(value2, suit2);
    }

    public void printHand() {
        System.out.println(playerName + " hand:");
        for (int i = 0; i < hand.length; i++) {
            if(hand[i] != null) {
                System.out.println(hand[i]);
            }
        }
        System.out.println();
    }

    public Card[] makeMadeHand(Card[] board) {
        //method used by evaluator class to create a made hand attached to a player
        Arrays.fill(madeHand, null);
        createPossCards(board);
        madeHand = Evaluator.makeMadeHand(possCards);

        return Arrays.copyOf(madeHand, madeHand.length);
    }

    public void printMadeHand() {
        //method used for testing only
        System.out.println(playerName + " made hand: " + getMadeHandName());
        for (int i = 0; i < madeHand.length; i++) {
            System.out.println(madeHand[i]);
        }
        System.out.println();
    }

    public void createPossCards(Card[] board) {
        //method used by evaluator class to help create a made hand attached to the player
        Arrays.fill(possCards, null);

        //populates possCards
        //only call this method after the river has been dealt
        for (int i = 0; i < possCards.length; i++) {
            if (i == 0) {
                possCards[0] = hand[0];
            } else if (i == 1) {
                possCards[1] = hand[1];
            } else {
                if (board[i - 2] != null) {
                    possCards[i] = board[i - 2];
                }
            }
        }

        //sorts possCards from highest val to lowest val
        for (int i = 0; i < possCards.length; i++) {
            for (int j = 0; j < possCards.length; j++) {
                if (j != i && possCards[i].getValue() > possCards[j].getValue()) {
                    Card temp = possCards[i];
                    possCards[i] = possCards[j];
                    possCards[j] = temp;
                }
            }
        }
    }

    public void printPossCards() {
        System.out.println(playerName + " possible cards:");
        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i] != null) {
                System.out.println(possCards[i]);
            }
        }
        System.out.println();
    }


    public String getMadeHandName() {
        return Evaluator.getMadeHandName(madeHand);
    }

    public int getMadeHandValue() {
        return Evaluator.getMadeHandValue(madeHand);
    }

    public Card[] getHand() {
        return Arrays.copyOf(hand, hand.length);
    }

    public boolean checkHasHand() {
        return hand[0] == null || hand[1] == null;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getStack() {
        return stack;
    }

    public int getStreetStartingStackSize() {
        return this.streetStartingStackSize;
    }

    public boolean checkPlayerAllIn() {
        return stack == 0;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public boolean getHasFolded() {
        return hasFolded;
    }

    public String toString() {
        if(hand[0] != null && hand[1] != null){
            return playerName + " Stack: " + getStack() + " Hand: " + hand[0] + ", " + hand[1];
        }else {
            return playerName + " Stack: " + getStack() + " Hand: None";
        }
    }

    //allows comparison of players based on their stack sizes.
    @Override
    public int compareTo(Object o) {
        Player p1 = (Player) o;
        if(p1.getStreetStartingStackSize() > this.streetStartingStackSize) {
            return -1;
        } else if (p1.getStreetStartingStackSize() < this.streetStartingStackSize) {
            return 1;
        } else {
            return 0;
        }
    }
}