package UI;

import Logic.Card;
import Logic.Deck;
import Logic.Game;
import Logic.Player;

public class Main {

    private static Table gameTable;

    public static void main(String argsp[]){
        if(argsp.length > 0) {
            gameTable = new Table(5);
            Deck deck = new Deck();
            Card[] board = new Card[5];
            Player[] players = Game.createPlayers(5, deck, 25000);

            Game.pickRandomDealer(players);
            Game.hand(players, board, deck, 25, 50);

            gameTable.setPlayerCard(0,0, players[0]);
        } else {
            //Logic.Game.testHands("FULL HOUSE", 1000, 3, 2);
            Deck deck = new Deck();
            Card[] board = new Card[5];
            Player[] players = Game.createPlayers(5, deck, 25000);

            Game.pickRandomDealer(players);
            for(int i = 0; i < 10; i++) {
                Game.hand(players, board, deck, 25, 50);
            }
        }
    }

    public static void callAction(){

    }

    public static void foldAction(){

    }

    public static void raiseAction(){

    }

    public static void checkAction(){

    }
}
