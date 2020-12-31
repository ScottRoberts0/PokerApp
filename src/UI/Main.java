package UI;

import Logic.Card;
import Logic.Deck;
import Logic.Game;
import Logic.Player;

import javax.swing.*;

public class Main {

    private static Table gameTable;

    public static void main(String argsp[]){
        if(argsp.length > 0) {
            gameTable = new Table(9);
        }else {
            //Logic.Game.testHands("FULL HOUSE", 1000, 3, 2);

            Deck deck = new Deck();
            Card[] board = new Card[5];
            Player[] players = Game.createPlayers(5, deck, 25000);
            Game.pickRandomDealer(players);

            for (int i = 0; i < 10; i++) {
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

    public static void callAction(){
        System.out.println("Call");
        gameTable.setPlayerCard(0, 0, 12, 1);
    }

    public static void foldAction(){
        System.out.println("Fold");
        gameTable.setPlayerCard(0, 1, 12, 2);
    }

    public static void raiseAction(){
        System.out.println("Raise");
        gameTable.setPlayerCard(0, 0, 11, 0);
    }

    public static void checkAction(){
        System.out.println("Check");
        gameTable.setPlayerCard(0, 1, 5, 3);
    }
}
