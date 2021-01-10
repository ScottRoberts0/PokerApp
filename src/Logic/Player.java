package Logic;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {
    private final int playerNum;
    private String playerName;
    private int moneyInPot;
    private int stack;
    private final Deck deck;
    private boolean hasFolded;

    private Card[] hand;
    private Card[] possCards;
    private Card[] madeHand;

    public Player(int playerNum, Deck deck) {
        hand = new Card[2];
        possCards = new Card[7];
        madeHand = new Card[5];
        this.playerNum = playerNum;
        this.deck = deck;
    }

    public Player(int playerNum, Deck deck, int stack) {
        hand = new Card[2];
        possCards = new Card[7];
        madeHand = new Card[5];
        this.playerNum = playerNum;
        this.deck = deck;
        this.stack = stack;
        this.playerName = "Player " + playerNum;
    }

    public Player(int playerNum, Deck deck, int stack, String playerName) {
        hand = new Card[2];
        possCards = new Card[7];
        madeHand = new Card[5];
        this.playerNum = playerNum;
        this.deck = deck;
        this.stack = stack;
        this.playerName = playerName;
    }



    public void resetHand() {
        Arrays.fill(hand, null);
    }

    public void refundBet(int bet, Pot pot) {
        stack += bet;
        pot.removeFromPot(bet);
        moneyInPot -= bet;
    }

    public void postBlind(int betSize, Pot mainPot) {
        if(stack - betSize < 0) {
            betSize = stack;
        }

        stack -= betSize;
        //Game.addToPot(betSize);
        mainPot.addToPot(betSize, playerNum);
        mainPot.addPlayerToPot(this);
        //bets[playerNum] = betSize;
        moneyInPot = betSize;
    }

    public void raise(int betSize, Pot pot) {
        if(stack - betSize == 0) {
            moneyInPot += betSize;

            stack -= betSize;
            pot.addToPot(betSize, playerNum);
            if(!pot.containsPlayer(this)) {
                pot.addPlayerToPot(this);
            }
        } else {
            stack -= betSize - pot.getBets()[playerNum];
            pot.addToPot(betSize - pot.getBets()[playerNum], playerNum);
            if(!pot.containsPlayer(this)) {
                pot.addPlayerToPot(this);
            }

            moneyInPot = betSize;
        }

        pot.setPlayerActed(playerNum, true);

        System.out.println(playerName + " raises to " + betSize);
        System.out.println();
    }

    public void call(Pot pot) {
        int callSize = Game.getHighestBet() - pot.getBets()[playerNum];

        if(stack - callSize < 0) {
            callSize = stack;
        }

        moneyInPot += callSize;

        stack -= callSize;
        pot.addToPot(callSize, playerNum);
        if(!pot.containsPlayer(this)) {
            pot.addPlayerToPot(this);
        }

        pot.setPlayerActed(playerNum, true);

        System.out.println(playerName + " calls " + callSize);
        System.out.println();
    }

    public void fold(ArrayList<Pot> pots) {
        moneyInPot = 0;
        resetHand();

        //when a player folds, they should be removed from every pot
        for(Pot pot : pots) {
            pot.removePlayerFromPot(this);
            pot.setPlayerActed(playerNum, false);
        }

        this.hasFolded = true;

        System.out.println(playerName + " folds");
        System.out.println();
    }

    public void check(Pot pot) {
        pot.setPlayerActed(playerNum, true);

        System.out.println(playerName + " checks");
        System.out.println();
    }

    public void win(int potSize) {
        stack += potSize;
        System.out.println(playerName + " wins " + potSize + " satoshis with a " + getMadeHandName() + "!");
        System.out.println();
    }

    public void resetMoneyInPot() {
        moneyInPot = 0;
    }

    public void resetFolded() {
        this.hasFolded = false;
    }

    public void resetStack() {
        this.stack = Game.getStartingStackSize();
    }

    public void drawHand() {
        hand[0] = deck.drawCard();
        hand[1] = deck.drawCard();
    }

    public void drawHand(String value1, String suit1, String value2, String suit2) {
        hand[0] = deck.drawCard(value1, suit1);
        hand[1] = deck.drawCard(value2, suit2);
    }

    public void printHand() {
        System.out.println(playerName + " hand:");
        for (int i = 0; i < hand.length; i++) {
            System.out.println(hand[i]);
        }
        System.out.println();
    }

    public Card[] makeMadeHand(Card[] board) {
        Arrays.fill(madeHand, null);
        createPossCards(board);
        madeHand = Evaluator.makeMadeHand(possCards);

        return Arrays.copyOf(madeHand, madeHand.length);
    }

    public void printMadeHand() {
        System.out.println(playerName + " made hand: " + getMadeHandName());
        for (int i = 0; i < madeHand.length; i++) {
            System.out.println(madeHand[i]);
        }
        System.out.println();
    }

    public void createPossCards(Card[] board) {
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

    public boolean checkPlayerAllIn() {
        return stack == 0;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getMoneyInPot() {
        return moneyInPot;
    }

    public boolean getHasFolded() {
        return hasFolded;
    }

    public String toString() {
        return playerName + " Stack: " + getStack();
    }
}
