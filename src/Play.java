import java.util.Scanner;

public class Play {
    public static void main(String[] args) {
        //Game.testHands(1000, 3);
        Scanner input = new Scanner(System.in);
        Deck deck = new Deck();
        Card[] board = new Card[5];

        //System.out.println("Number of players: ");
        int numPlayers = /*input.nextInt();*/ 5;
        Player[] players = new Player[numPlayers];

        //System.out.println("Starting stack size: ");
        int startingStacks = /*input.nextInt();*/ 10000;
        for(int i = 0; i < players.length; i++) {
            players[i] = new Player(i + 1, deck, startingStacks);
        }

        Game.preflop(players);
        Game.flop(deck, board, players);
    }
}
