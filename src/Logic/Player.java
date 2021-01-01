package Logic;
import UI.Main;

import java.util.Arrays;

public class Player {
    private int playerNum;
    private int stack;
    private Deck deck;

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
    }

    public void postBlind(int betSize, int[] bets) {
        stack -= betSize;
        Main.addToPot(betSize);
        bets[playerNum] = betSize;
    }

    public void bet(int betSize, int[] bets, boolean[] playerHasActed) {
        int highestBet = -1;
        for(int bet : bets) {
            if(bet > highestBet) {
                highestBet = bet;
            }
        }

        int raiseSize = highestBet + betSize;
        stack -= raiseSize - bets[playerNum];

        Main.addToPot(raiseSize - bets[playerNum]);

        bets[playerNum] = raiseSize;
        playerHasActed[playerNum] = true;

        System.out.println("Player " + playerNum + " raises to " + raiseSize);
    }

    public void call(int[] bets, boolean[] playerHasActed) {
        int highestBet = -1;
        for(int bet : bets) {
            if(bet > highestBet) {
                highestBet = bet;
            }
        }


        int callSize = highestBet - bets[playerNum];
        Main.addToPot(callSize);

        bets[playerNum] = highestBet;
        playerHasActed[playerNum] = true;

        stack -= callSize;

        System.out.println("Player " + playerNum + " calls " + callSize);
    }

    public void fold(int[] bets, boolean[] playersInHand) {
        bets[playerNum] = 0;
        playersInHand[playerNum] = false;
        //Arrays.fill(hand, null);
        System.out.println("Player " + playerNum + " folds");
    }

    public void check(boolean[] playerHasActed) {
        playerHasActed[playerNum] = true;
        System.out.println("Player " + playerNum + " checks");
    }

    public void win(int potSize) {
        stack += potSize;
        System.out.println("Player " + playerNum + " wins " + potSize + "!");
    }

    public Card[] getHand() {
        return Arrays.copyOf(hand, hand.length);
    }

    public Card[] makeMadeHand(Card[] board) {
        Arrays.fill(madeHand, null);
        createPossCards(board);
        madeHand = Evaluator.makeMadeHand(possCards);

        return Arrays.copyOf(madeHand, madeHand.length);
    }

    public void printMadeHand() {
        System.out.println("Player " + playerNum + " made hand: " + getMadeHandName());
        for (int i = 0; i < madeHand.length; i++) {
            System.out.println(madeHand[i]);
        }
        System.out.println();
    }

    public String getMadeHandName() {
        return Evaluator.getMadeHandName(madeHand);
    }

    public int getMadeHandValue() {
        return Evaluator.getMadeHandValue(madeHand);
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
        System.out.println("Player " + playerNum + " possible cards:");
        for (int i = 0; i < possCards.length; i++) {
            if (possCards[i] != null) {
                System.out.println(possCards[i]);
            }
        }
        System.out.println();
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
        System.out.println("Player " + playerNum + " hand:");
        for (int i = 0; i < hand.length; i++) {
            System.out.println(hand[i]);
        }
        System.out.println();
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getStack() {
        return stack;
    }

    public String toString() {
        return "Player " + getPlayerNum() + " stack: " + getStack() + " cards: " + hand[0].toString() + ", " + hand[1].toString();
    }
}
