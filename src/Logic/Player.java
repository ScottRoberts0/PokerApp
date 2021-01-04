package Logic;

import UI.Main;

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

    //various utility arrays
    private int[] counter = new int[15];
    private int[] suitCounter = new int[4];
    private int[][] specialCounter = new int[4][15];

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
        hand[0] = null;
        hand[1] = null;
    }

    public void postBlind(int betSize, int[] bets) {
        if(stack - betSize < 0) {
            betSize = stack;
        }

        stack -= betSize;
        Main.addToPot(betSize);
        bets[playerNum] = betSize;
        moneyInPot = betSize;
    }

    public void raise(int betSize, int[] bets, boolean[] playerHasActed) {
        stack -= betSize - bets[playerNum];
        Main.addToPot(betSize - bets[playerNum]);
        bets[playerNum] = betSize;
        moneyInPot = betSize;

        playerHasActed[playerNum] = true;

        System.out.println(playerName + " raises to " + betSize);
        System.out.println();
    }

    public void call(int[] bets, boolean[] playerHasActed) {
        int callSize = Game.getHighestBet(bets) - bets[playerNum];

        stack -= callSize;
        Main.addToPot(callSize);
        bets[playerNum] += callSize;
        moneyInPot = bets[playerNum];

        playerHasActed[playerNum] = true;

        System.out.println(playerName + " calls " + callSize);
        System.out.println();
    }

    public void fold(int[] bets, boolean[] playersInHand) {
        bets[playerNum] = 0;
        moneyInPot = 0;
        playersInHand[playerNum] = false;
        this.hasFolded = true;
        System.out.println(playerName + " folds");
        System.out.println();
    }

    public void check(boolean[] playerHasActed) {
        playerHasActed[playerNum] = true;
        System.out.println(playerName + " checks");
        System.out.println();
    }

    public void win(int potSize) {
        stack += potSize;
        System.out.println(playerName + " wins " + potSize + " satoshis!");
        System.out.println();
    }

    public void resetMoneyInPot() {
        moneyInPot = 0;
    }

    public void resetFolded() {
        this.hasFolded = false;
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

    public int getPlayerNum() {
        return playerNum;
    }

    public int getStack() {
        return stack;
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
        return "Player " + getPlayerNum() + " stack: " + getStack() + " cards: " + hand[0].toString() + ", " + hand[1].toString();
    }
}
