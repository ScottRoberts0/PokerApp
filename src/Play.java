import java.util.Scanner;

public class Play {
    public static void main(String[] args) {
        //Game.testHands("FULL HOUSE", 1000, 3, 2);

        Scanner input = new Scanner(System.in);
        Deck deck = new Deck();
        Card[] board = new Card[5];
        Player[] players = Game.createPlayers(5, deck, 25000);
        Game.pickRandomDealer(players);

        //Let's start really simple... just get blinds posted and hands dealt first.
        for(int i = 0; i < 10; i++) {
            Game.preFlop(players, 25, 50);
            Game.flop(players, board, deck);
            Game.turn(players, board, deck);
            Game.river(players, board, deck);

            Game.nextDealer(players);
            deck.shuffle();

            System.out.println("==========================");
        }
    }
}
