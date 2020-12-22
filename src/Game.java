public class Game {
    public static void main(String[] args) {
        Deck deck = new Deck();
        Player[] players = new Player[4];
        for(int i = 0; i < players.length; i++) {
            players[i] = new Player(i + 1, deck);
        }

        Card[] board = new Card[5];

        for(Player player : players) {
            player.drawHand();
        }

        dealFlop(board, deck);
        dealTurn(board, deck);
        dealRiver(board, deck);

        printBoard(board);

        for(Player player : players) {
            player.printHand();
        }

        int winner = Evaluator.findWinner(players, board);
        System.out.println("Player " + winner + " wins!");


        //equityCalculator(players[0], "ace", "spades", "king", "hearts", players[1], board, deck);
    }
    public static void dealFlop(Card[] board, Deck deck) {
        for(int i = 0; i < 3; i++) {
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
    public static void equityCalculator(Player player1, String value1, String suit1, String value2, String suit2, Player player2, Card[] board, Deck deck) {
        double player1Wins = 0;
        double player2Wins = 0;
        double ties = 0;
        for(int i = 0; i < 1000000; i++){
            player1.drawHand(value1, suit1, value2, suit2);
            player2.drawHand();
            dealFlop(board, deck);
            dealTurn(board, deck);
            dealRiver(board, deck);

            player1.createToolArrays(board);
            player2.createToolArrays(board);

            player1.makeMadeHand();
            player2.makeMadeHand();

            if(player1.compareHands(player2) == 1) {
                player1Wins++;
            } else if(player1.compareHands(player2) == -1) {
                player2Wins++;
            } else {
                ties++;
            }

            deck.shuffle();
        }

        double p1Equity = player1Wins / (player1Wins + player2Wins + ties);
        double p2Equity = player2Wins / (player1Wins + player2Wins + ties);

        System.out.println("Player one wins: " + player1Wins);
        System.out.println("Plyaer two wins: " + player2Wins);
        System.out.println("Hands ending in a tie: " + ties);
        System.out.println();
        System.out.println("Player one equity: " + p1Equity + "%");
        System.out.println("Plyaer two equity: " + p2Equity + "%");
    }
}
