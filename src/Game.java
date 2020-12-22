import java.util.Arrays;

public class Game {
    public static void main(String[] args) {
        Deck deck = new Deck();
        Player playerOne = new Player(1, deck);
        Player playerTwo = new Player(2, deck);
        int playerOneWins = 0;
        int playerTwoWins = 0;
        int ties = 0;
        Card[] board = new Card[5];

        int count = 0;
        while (count < 1000000) {
            //deal cards
            playerOne.drawHand("ace", "hearts", "king", "spades");
            playerTwo.drawHand("queen", "hearts", "queen", "diamonds");
            dealFlop(board, deck);
            dealTurn(board, deck);
            dealRiver(board, deck);

            //process hands
            playerOne.createToolArrays(board);
            playerOne.makeMadeHand();
            playerTwo.createToolArrays(board);
            playerTwo.makeMadeHand();

            /* TESTING STUFF:
            int[] playerOneCounter = playerOne.getCounter();
            int[] playerTwoCounter = playerTwo.getCounter();
            String oneCounterString = "";
            String twoCounterString = "";

            for(int i = 0; i < playerOneCounter.length; i++) {
                oneCounterString += Integer.toString(playerOneCounter[i]);
            }

            for(int i = 0; i < playerOneCounter.length; i++) {
                twoCounterString += Integer.toString(playerTwoCounter[i]);
            }

            int[] playerOneSuitCounter = playerOne.getSuitCounter();
            int[] playerTwoSuitCounter = playerTwo.getSuitCounter();

            String oneSuitCounterString = "";
            String twoSuitCounterString = "";

            for(int i = 0; i < playerOneSuitCounter.length; i++) {
                oneSuitCounterString += Integer.toString(playerOneSuitCounter[i]);
            }

            for(int i = 0; i < playerTwoSuitCounter.length; i++) {
                twoSuitCounterString += Integer.toString(playerTwoSuitCounter[i]);
            }*/

            //if(playerOne.getMadeHandName().equals("ROYAL FLUSH") || playerTwo.getMadeHandName().equals("ROYAL FLUSH")) {
            //printBoard(board);
            //playerOne.printHand();
            //playerTwo.printHand();

            //System.out.println(oneCounterString);
            //System.out.println(oneSuitCounterString);
            //playerOne.printPossCards();
            //playerOne.printMadeHand();

            //System.out.println(twoCounterString);
            //System.out.println(twoSuitCounterString);
            //playerTwo.printPossCards();
            //playerTwo.printMadeHand();

            if (playerOne.compareHands(playerTwo) == 1) {
                //System.out.println("Player ONE wins!");
                playerOneWins++;
            } else if (playerOne.compareHands(playerTwo) == -1) {
                //System.out.println("Player TWO wins!");
                playerTwoWins++;
            } else {
                //System.out.println("It is a tie.");
                ties++;
            }
            //System.out.println("=============");
            //}

            deck.shuffle();
            count++;
        }

        System.out.println("Player one wins: " + playerOneWins);
        System.out.println("Player two wins: " + playerTwoWins);
        System.out.println("Ties: " + ties);
        System.out.println("Player one equity: " + (int) (((double) playerOneWins / ((double) playerOneWins + (double) playerTwoWins + (double) ties)) * 100) + "%");
        System.out.println("Player two equity: " + (int) (((double) playerTwoWins / ((double) playerOneWins + (double) playerTwoWins + (double) ties)) * 100) + "%");
        //System.out.println("Percentage of games ending in a tie: " + (int)(((double)ties / ((double)playerOneWins + (double)playerTwoWins + (double)ties)) * 100) + "%");
    }

    public static void dealFlop(Card[] board, Deck deck) {
        for (int i = 0; i < 3; i++) {
            board[i] = deck.drawCard();
        }
    }

    public static void dealFlop(Card[] board, Deck deck, String value1, String suit1, String value2, String suit2, String value3, String suit3) {
        board[0] = deck.drawCard(value1, suit1);
        board[1] = deck.drawCard(value2, suit2);
        board[2] = deck.drawCard(value3, suit3);
    }

    public static void dealTurn(Card[] board, Deck deck) {
        board[3] = deck.drawCard();
    }

    public static void dealTurn(Card[] board, Deck deck, String value1, String suit1) {
        board[3] = deck.drawCard(value1, suit1);
    }

    public static void dealRiver(Card[] board, Deck deck) {
        board[4] = deck.drawCard();
    }

    public static void dealRiver(Card[] board, Deck deck, String value1, String suit1) {
        board[4] = deck.drawCard(value1, suit1);
    }

    public static void printBoard(Card[] board) {
        System.out.println("Board:");
        for (Card card : board) {
            if (card != null) {
                System.out.println(card);
            }
        }
        System.out.println();
    }
}
