import java.util.Arrays;
import java.util.Scanner;

public class Player {
    private int playerNum;
    private int stack;
    private Deck deck;

    private Card[] hand;
    private Card[] possCards;
    private Card[] madeHand;
    private boolean hasFolded;
    private int lastVPIP;

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
        this.hasFolded = false;
    }

    public Player(int playerNum, Deck deck, int stack) {
        hand = new Card[2];
        possCards = new Card[7];
        madeHand = new Card[5];
        this.playerNum = playerNum;
        this.deck = deck;
        this.stack = stack;
        this.hasFolded = false;
    }



    public char askAction(char action) {
        if(action == 'c') {
            return 'c';
        } else if(action == 'f') {
            return 'f';
        } else if(action == 'x') {
            return 'x';
        } else if(action == 'r') {
            return 'r';
        } else if(action == 'b') {
            return 'b';
        } else {
            return 'z';
        }
    }

    public void bet(int betSize) {
        stack -= betSize - lastVPIP;
        Game.addToPot(betSize - lastVPIP);
        lastVPIP = betSize - lastVPIP;
    }

    public void raise(int betSize) {

    }

    public void call() {
        stack -= Game.getAmountToCall();
        lastVPIP = Game.getAmountToCall();
        Game.addToPot(lastVPIP);
        System.out.println("Player " + getPlayerNum() + " calls " + lastVPIP);
    }

    public void check() {
        System.out.println("Player " + getPlayerNum() + " checks");
    }

    public void fold() {
        hasFolded = true;

        System.out.println("Player " + getPlayerNum() + " folds");
    }

    public int getLastVPIP() {
        return lastVPIP;
    }

    public void win(int pot) {
        stack += pot;
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
        this.hasFolded = false;
        this.lastVPIP = 0;
        hand[0] = deck.drawCard();
        hand[1] = deck.drawCard();
    }

    public void drawHand(String value1, String suit1, String value2, String suit2) {
        this.hasFolded = false;
        this.lastVPIP = 0;
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
        return "Player " + getPlayerNum() + " stack: " + getStack();
    }

    public boolean hasFolded() {
        return hasFolded;
    }
}
